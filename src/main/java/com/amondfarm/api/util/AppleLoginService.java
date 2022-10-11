package com.amondfarm.api.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.amondfarm.api.domain.enums.user.ProviderType;
import com.amondfarm.api.dto.WithdrawRequest;
import com.amondfarm.api.security.dto.LoginRequest;
import com.amondfarm.api.security.dto.UserInfoResponse;
import com.amondfarm.api.util.client.AppleClient;
import com.amondfarm.api.util.dto.ApplePublicKeyResponse;
import com.amondfarm.api.util.dto.AppleToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppleLoginService implements OAuthService {

	@Value("#{environment['oauth2.apple.keyId']}")
	private String keyId;
	@Value("#{environment['oauth2.apple.teamId']}")
	private String teamId;
	@Value("#{environment['oauth2.apple.clientId']}")
	private String clientId;

	@Value("${oauth2.apple.keyPath}")
	private String keyPath;

	private final AppleClient appleClient;

	@Override
	public Optional<UserInfoResponse> getUserInfo(LoginRequest loginRequest) {

		Claims userInfo = getClaimsBy(loginRequest.getAccessToken());
		// 여기서부터 Identity Token 의 payload 들이 신뢰할 수 있는 값들로 증명 완료.
		// payload 에서 apple 고유 계정 id 등 중요 요소 획득해서 사용하면 됨.

		return parseUserInfo(userInfo);
	}

	// Identity Token 검증
	public Claims getClaimsBy(String identityToken) {
		try {
			ApplePublicKeyResponse response = appleClient.getAppleAuthPublicKey();

			String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));
			Map<String, String> header = new ObjectMapper().readValue(
				new String(Base64.getDecoder().decode(headerOfIdentityToken), "UTF-8"), Map.class);
			ApplePublicKeyResponse.Key key = response.getMatchedKeyBy(header.get("kid"), header.get("alg"))
				.orElseThrow(() -> new NullPointerException("Apple ID Server 로부터 Public Key 를 가져오는데에 실패했습니다."));

			byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
			byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

			BigInteger n = new BigInteger(1, nBytes);
			BigInteger e = new BigInteger(1, eBytes);

			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
			KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(identityToken).getBody();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (SignatureException | MalformedJwtException e) {
			e.printStackTrace();
			//토큰 서명 검증 or 구조 문제 (Invalid token)
		} catch (ExpiredJwtException e) {
			e.printStackTrace();
			//토큰이 만료됐기 때문에 클라이언트는 토큰을 refresh 해야함.
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private Optional<UserInfoResponse> parseUserInfo(Claims claims) {

		//Gson 라이브러리로 JSON파싱
		JsonObject userInfoObject = JsonParser.parseString(new Gson().toJson(claims)).getAsJsonObject();
		// System.out.println("userInfoObject = " + userInfoObject.toString());
		JsonElement appleAlg = userInfoObject.get("sub");
		String userId = appleAlg.getAsString();

		//TODO : 애플 닉네임 파싱하기 + Response 데이터에 injection 하기
		return Optional.of(UserInfoResponse.builder()
			.loginId(userId)
			.providerType(ProviderType.APPLE)
			.build());
	}

	public AppleToken.Response generateAuthToken(String authorizationCode) throws IOException {

		return appleClient.getToken(AppleToken.Request.of(
			authorizationCode,
			clientId,
			createClientSecret(),
			"authorization_code"
		));
	}

	public String createClientSecret() throws IOException {
		Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
		Map<String, Object> jwtHeader = new HashMap<>();
		jwtHeader.put("kid", keyId);
		jwtHeader.put("alg", "ES256");

		return Jwts.builder()
			.setHeaderParams(jwtHeader)
			.setIssuer(teamId)    // Apple Developer 페이지에 명시되어 있는 Team ID (우측 상단에 있음)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(expirationDate)
			.setAudience("https://appleid.apple.com")
			.setSubject(clientId)    // Apple Developer 페이지에 App Bundle ID (com.xxx.xxx 형식)
			.signWith(getPrivateKey(), SignatureAlgorithm.ES256)
			.compact();
	}

	private PrivateKey getPrivateKey() throws IOException {
		log.info(keyPath);

		ClassPathResource resource = new ClassPathResource(keyPath);
		log.info("exist : " + resource.exists());
		String privateKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));

		Reader pemReader = new StringReader(privateKey);
		PEMParser pemParser = new PEMParser(pemReader);
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		PrivateKeyInfo object = (PrivateKeyInfo)pemParser.readObject();
		return converter.getPrivateKey(object);
	}

	public void revoke(WithdrawRequest request) throws IOException {

		AppleToken.Response response = generateAuthToken(request.getAuthorizationCode());

		if (response.getAccess_token() != null) {
			appleClient.revoke(AppleToken.RevokeRequest.of(
					clientId,
					createClientSecret(),
					response.getAccess_token()
				)
			);
		}
	}
}

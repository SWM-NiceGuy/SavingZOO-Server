package com.amondfarm.api.security.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.ProviderType;
import com.amondfarm.api.security.dto.LoginTokenRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppleLoginUtil implements OAuthUtil {

	@Value("#{environment['oauth2.apple.userInfoUri']}")
	private URL requestURL;
	@Value("#{environment['oauth2.apple.logoutUri']}")
	private String logoutURL;

	@Override
	public User createEntity(LoginUserInfoDto loginUserInfoDto) {
		return User.from(loginUserInfoDto);
	}

	@Override
	public Optional<LoginUserInfoDto> requestUserInfo(LoginTokenRequest loginTokenRequest) {

		Optional<LoginUserInfoDto> loginUserInfoDto = Optional.empty();
		try {
			HttpURLConnection conn = getHttpURLConnection();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new NoSuchElementException("Provider 에게서 정보를 받아올 수 없습니다.");
			}

			String result = getResult(conn.getInputStream());
			loginUserInfoDto = parseUserInfo(result, loginTokenRequest.getAccessToken());

		} catch (IOException e) {
			e.printStackTrace();
		}

		return loginUserInfoDto;
	}

	private HttpURLConnection getHttpURLConnection() throws IOException {

		HttpURLConnection conn = (HttpURLConnection)requestURL.openConnection();
		conn.setRequestMethod("GET");
		return conn;
	}

	private String getResult(InputStream inputStream) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		StringBuilder result = new StringBuilder();

		while ((line = br.readLine()) != null) {
			result.append(line);
		}
		br.close();

		return result.toString();
	}

	private Optional<LoginUserInfoDto> parseUserInfo(String result, String identityToken) {

		//Gson 라이브러리로 JSON 파싱
		JsonObject keys = JsonParser.parseString(result).getAsJsonObject();
		JsonArray keyArray = (JsonArray)keys.get("keys");

		System.out.println("[util] keys = " + keys);
		System.out.println("[util] keyArray = " + keyArray);

		//클라이언트로부터 가져온 identity token String decode
		String[] decodeArray = identityToken.split("\\.");
		String header = new String(Base64.getDecoder().decode(decodeArray[0]));

		//apple 에서 제공해주는 kid 값과 일치하는지 알기 위해
		JsonElement kid = JsonParser.parseString(header).getAsJsonObject().get("kid");
		JsonElement alg = JsonParser.parseString(header).getAsJsonObject().get("alg");

		//써야하는 Element (kid, alg 일치하는 element)
		JsonObject availableObject = null;

		for (int i = 0; i < keyArray.size(); i++) {
			JsonObject appleObject = keyArray.get(i).getAsJsonObject();
			JsonElement appleKid = appleObject.get("kid");
			JsonElement appleAlg = appleObject.get("alg");

			if (Objects.equals(appleKid, kid) && Objects.equals(appleAlg, alg)) {
				availableObject = appleObject;
				break;
			}
		}

		//일치하는 공개키 없음
		if (ObjectUtils.isEmpty(availableObject)) {
			throw new NoSuchElementException("일치하는 공개키가 없습니다.");
		}

		PublicKey publicKey = this.getPublicKey(availableObject);

		//--> 여기까지 검증

		Claims userInfo = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(identityToken).getBody();
		JsonObject userInfoObject = JsonParser.parseString(new Gson().toJson(userInfo)).getAsJsonObject();
		JsonElement appleAlg = userInfoObject.get("sub");
		String userId = appleAlg.getAsString();

		System.out.println("[util] userId = " + userId);

		return Optional.of(LoginUserInfoDto.builder()
			.loginId(userId)
			.providerType(ProviderType.APPLE)
			.build());
	}

	public PublicKey getPublicKey(JsonObject object) {
		String nStr = object.get("n").toString();
		String eStr = object.get("e").toString();

		byte[] nBytes = Base64.getUrlDecoder().decode(nStr.substring(1, nStr.length() - 1));
		byte[] eBytes = Base64.getUrlDecoder().decode(eStr.substring(1, eStr.length() - 1));

		BigInteger n = new BigInteger(1, nBytes);
		BigInteger e = new BigInteger(1, eBytes);

		try {
			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			return publicKey;
		} catch (Exception exception) {
			throw new IllegalStateException("Public Key 를 가져오는 데에 실패했습니다.");
		}
	}
}

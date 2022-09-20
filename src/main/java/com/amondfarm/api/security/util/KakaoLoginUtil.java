package com.amondfarm.api.security.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amondfarm.api.domain.User;
import com.amondfarm.api.domain.enums.ProviderType;
import com.amondfarm.api.security.dto.LoginTokenRequest;
import com.amondfarm.api.security.dto.LoginUserInfoDto;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KakaoLoginUtil implements OAuthUtil {

	@Value("#{environment['oauth2.kakao.userInfoUri']}")
	private URL requestURL;
	@Value("#{environment['oauth2.kakao.logoutUri']}")
	private String logoutURL;

	@Override
	public User createEntity(LoginUserInfoDto loginUserInfoDto) {
		return User.from(loginUserInfoDto);
	}

	@Override
	public Optional<LoginUserInfoDto> requestUserInfo(LoginTokenRequest loginTokenRequest) {

		Optional<LoginUserInfoDto> loginUserInfoDto = Optional.empty();
		try {
			HttpURLConnection conn = getHttpURLConnection(loginTokenRequest.getAccessToken());

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new NoSuchElementException("Provider에게서 정보를 받아올 수 없습니다.");
			}

			String result = getResult(conn.getInputStream());
			loginUserInfoDto = parseUserInfo(result);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return loginUserInfoDto;
	}

	// public void logout(String accessToken) {
	// 	try {
	// 		HttpURLConnection conn = getHttpURLConnection(accessToken);
	//
	// 		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
	// 			throw new NoSuchElementException("Provider에게서 정보를 받아올 수 없습니다.");
	// 		}
	//
	// 		String result = getResult(conn.getInputStream());
	//
	// 		System.out.println("결과");
	// 		System.out.println(result);
	// 	} catch (IOException e) {
	// 		e.printStackTrace();
	// 	}
	// }

	private HttpURLConnection getHttpURLConnection(String accessToken) throws IOException {

		HttpURLConnection conn = (HttpURLConnection)requestURL.openConnection();

		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		//전송할 header 작성, access_token전송
		//access_token을 이용하여 사용자 정보 조회
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);

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

	private Optional<LoginUserInfoDto> parseUserInfo(String result) {

		//Gson 라이브러리로 JSON파싱
		JsonObject element = JsonParser.parseString(result).getAsJsonObject();

		System.out.println("[util] element : " + element);

		String id = element.get("id").getAsString();
		String email = element.get("kakao_account").getAsJsonObject()
			.get("email").getAsString();

		System.out.println("[util] id : " + id);
		System.out.println("[util] email : " + email);

		return Optional.of(LoginUserInfoDto.builder()
			.loginId(id)
			.providerType(ProviderType.KAKAO)
			.email(email)
			.build());
	}

	public void createKakaoUser(String token) {

		try {
			HttpURLConnection conn = (HttpURLConnection)requestURL.openConnection();

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

			//요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = "";
			String result = "";

			while ((line = br.readLine()) != null) {
				result += line;
			}
			System.out.println("[util] response body : " + result);

			//Gson 라이브러리로 JSON파싱
			JsonParser parser = new JsonParser();
			JsonElement element = parser.parse(result);

			int id = element.getAsJsonObject().get("id").getAsInt();
			boolean hasEmail = element.getAsJsonObject()
				.get("kakao_account")
				.getAsJsonObject()
				.get("has_email")
				.getAsBoolean();
			String email = "";
			if (hasEmail) {
				email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
			}

			System.out.println("[util] id : " + id);
			System.out.println("[util] email : " + email);

			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

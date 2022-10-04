package com.amondfarm.api.util;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.amondfarm.api.dto.FcmMessage;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {

	@Value("${fcm.url}")
	private String FCM_URL;

	// @Value("$classpath:firebase-adminsdk.json")
	// private Resource fcmConfigFilePath;
	// @Value("${fcm.filePath}")
	// private String fcmConfigFilePath;

	@Value("${fcm.key.path}")
	private String FCM_PRIVATE_KEY_PATH;

	@Value("${fcm.key.scope}")
	private String fireBaseScope;

	private final ObjectMapper objectMapper;


	public void sendMessageTo(String targetToken, String title, String body) {
		try {
			String message = makeMessage(targetToken, title, body);

			OkHttpClient client = new OkHttpClient();
			RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
			Request request = new Request.Builder()
				.url(FCM_URL)
				.post(requestBody)
				.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
				.addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
				.build();

			Response response = client.newCall(request).execute();

			System.out.println(response.body().string());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String makeMessage(String targetToken, String title, String body) throws
		JsonParseException,
		JsonProcessingException {
		FcmMessage fcmMessage = FcmMessage.builder()
			.message(FcmMessage.Message.builder()
				.token(targetToken)
				.notification(FcmMessage.Notification.builder()
					.title(title)
					.body(body)
					.image(null)
					.build()
				).build()).validateOnly(false).build();

		return objectMapper.writeValueAsString(fcmMessage);
	}

	private String getAccessToken() throws IOException {

		log.info("file path : " + FCM_PRIVATE_KEY_PATH);

		GoogleCredentials googleCredentials = GoogleCredentials
			// .fromStream(fcmConfigFilePath.getInputStream())
			.fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
			.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

		// new ByteArrayInputStream(edited_auth_json.getBytes())
		googleCredentials.refreshIfExpired();
		return googleCredentials.getAccessToken().getTokenValue();

	}
}

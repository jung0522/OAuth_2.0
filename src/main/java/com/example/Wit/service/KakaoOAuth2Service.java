package com.example.Wit.service;

import com.example.Wit.dto.KakaoUserInfoDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoOAuth2Service {

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    @Value("${kakao.token.url}")
    private String tokenUrl;

    @Value("${kakao.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // 🔹 1️⃣ 액세스 토큰 발급
    public String getAccessToken(String authorizationCode) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "authorization_code");
        requestBody.put("client_id", clientId);
        requestBody.put("redirect_uri", redirectUri);
        requestBody.put("code", authorizationCode);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenUrl, requestBody, JsonNode.class);
        return response.getBody().get("access_token").asText();
    }

    // 🔹 2️⃣ 카카오 사용자 정보 가져오기
    public KakaoUserInfoDto getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, JsonNode.class);
        JsonNode userInfo = response.getBody();

        String email = userInfo.get("kakao_account").get("email").asText();
        String nickname = userInfo.get("properties").get("nickname").asText();

        return new KakaoUserInfoDto(email, nickname);
    }
}

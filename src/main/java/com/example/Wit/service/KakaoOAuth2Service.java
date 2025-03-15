package com.example.Wit.service;

import com.example.Wit.dto.KakaoUserInfoDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;

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

    // 클라이언트 ID 반환
    public String getClientId() {
        return clientId;
    }

    // 리다이렉트 URI 반환
    public String getRedirectUri() {
        return redirectUri;
    }

    public String getAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 중요!

        // 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenUrl, request, JsonNode.class);
            System.out.println("카카오 응답: " + response.getBody()); // 디버깅용

            if (response.getBody() != null && response.getBody().has("access_token")) {
                return response.getBody().get("access_token").asText();
            }
        } catch (HttpClientErrorException e) {
            System.err.println("카카오 로그인 오류: " + e.getResponseBodyAsString()); // 디버깅용
            throw new RuntimeException("카카오 로그인 중 오류 발생: " + e.getMessage());
        }

        throw new RuntimeException("액세스 토큰을 받아올 수 없습니다.");
    }


    public KakaoUserInfoDto getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, JsonNode.class);
            if (response.getBody() != null) {
                JsonNode userInfo = response.getBody();
                JsonNode kakaoAccount = userInfo.path("kakao_account");
                JsonNode profile = kakaoAccount.path("profile");

                String email = kakaoAccount.has("email") ? kakaoAccount.get("email").asText() : "no-email";
                String nickname = profile.has("nickname") ? profile.get("nickname").asText() : "사용자";
                String profileImage = profile.has("profile_image_url") ? profile.get("profile_image_url").asText() : null;

                return new KakaoUserInfoDto(email, nickname, profileImage);
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("카카오 사용자 정보를 가져오는 중 오류 발생: " + e.getMessage());
        }

        throw new RuntimeException("사용자 정보를 받아올 수 없습니다.");
    }

}

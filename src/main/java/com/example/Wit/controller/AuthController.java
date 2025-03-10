package com.example.Wit.controller;

import com.example.Wit.dto.KakaoUserInfoDto;
import com.example.Wit.entity.User;
import com.example.Wit.repository.UserRepository;
import com.example.Wit.service.KakaoOAuth2Service;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final UserRepository userRepository;

    public AuthController(KakaoOAuth2Service kakaoOAuth2Service, UserRepository userRepository) {
        this.kakaoOAuth2Service = kakaoOAuth2Service;
        this.userRepository = userRepository;
    }

    @GetMapping("/kakao/callback")
    public String kakaoLogin(@RequestParam("code") String code) {
        // 🔹 1️⃣ 액세스 토큰 발급
        String accessToken = kakaoOAuth2Service.getAccessToken(code);

        // 🔹 2️⃣ 카카오 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoOAuth2Service.getKakaoUserInfo(accessToken);

        // 🔹 3️⃣ DB에 유저 저장
        Optional<User> user = userRepository.findByEmail(kakaoUserInfo.getEmail());
        if (user.isEmpty()) {
            userRepository.save(new User(kakaoUserInfo.getEmail(), kakaoUserInfo.getNickname()));
        }

        return "로그인 성공: " + kakaoUserInfo.getNickname();
    }
}

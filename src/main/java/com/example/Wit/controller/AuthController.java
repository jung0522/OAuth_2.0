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
        String accessToken = kakaoOAuth2Service.getAccessToken(code);
        KakaoUserInfoDto kakaoUserInfo = kakaoOAuth2Service.getKakaoUserInfo(accessToken);

        Optional<User> optionalUser = userRepository.findByEmail(kakaoUserInfo.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // 닉네임과 프로필 이미지가 변경되었으면 업데이트
            if (!user.getNickname().equals(kakaoUserInfo.getNickname()) ||
                    !user.getProfileImage().equals(kakaoUserInfo.getProfileImage())) {
                user.setNickname(kakaoUserInfo.getNickname());
                user.setProfileImage(kakaoUserInfo.getProfileImage());  // 프로필 이미지 업데이트
                userRepository.save(user);
            }
        } else {
            // 새로운 사용자라면 프로필 이미지도 함께 저장
            userRepository.save(new User(kakaoUserInfo.getEmail(), kakaoUserInfo.getNickname(), kakaoUserInfo.getProfileImage()));
        }

        return "로그인 성공: " + kakaoUserInfo.getNickname();
    }

    @GetMapping("/user")
    public KakaoUserInfoDto getUserInfo(@RequestHeader("Authorization") String token) {
        if (!token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        String accessToken = token.substring(7);
        return kakaoOAuth2Service.getKakaoUserInfo(accessToken);
    }
}

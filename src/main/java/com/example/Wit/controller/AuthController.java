package com.example.Wit.controller;

import com.example.Wit.dto.KakaoUserInfoDto;
import com.example.Wit.entity.User;
import com.example.Wit.repository.UserRepository;
import com.example.Wit.service.JwtTokenService;
import com.example.Wit.service.KakaoOAuth2Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthController(KakaoOAuth2Service kakaoOAuth2Service, UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.kakaoOAuth2Service = kakaoOAuth2Service;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    // 카카오 로그인 페이지로 리다이렉트
    @GetMapping("/kakao")
    public RedirectView redirectToKakao() {
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" +
                kakaoOAuth2Service.getClientId() +
                "&redirect_uri=" + kakaoOAuth2Service.getRedirectUri();
        return new RedirectView(kakaoLoginUrl);
    }

    // 카카오 로그인 성공 후 callback 처리
    @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String code) {
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


        return ResponseEntity.ok("로그인 성공: " + kakaoUserInfo.getNickname() + ", accessToken: " + accessToken);
    }


    // 사용자 정보 반환 (테스트용)
    @GetMapping("/user")
    public KakaoUserInfoDto getUserInfo(@RequestHeader("Authorization") String token) {
        if (!token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token format");
        }
        String accessToken = token.substring(7);
        return kakaoOAuth2Service.getKakaoUserInfo(accessToken);
    }
}

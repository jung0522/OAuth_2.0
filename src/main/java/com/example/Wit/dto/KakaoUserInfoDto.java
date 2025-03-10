package com.example.Wit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfoDto {
    private String email;
    private String nickname;

    public KakaoUserInfoDto(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}

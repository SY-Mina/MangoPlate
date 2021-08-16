package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoUserInfo {
    private Integer id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    public KakaoUserInfo() {}

}

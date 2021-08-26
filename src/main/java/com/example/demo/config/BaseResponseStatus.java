package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_INVALID_PASSWORD(false, 2018, "비밀번호는 6자 이상 12자 이하입니다."),
    POST_USERS_INVALID_PHONE(false, 2019, "핸드폰 형식을 확인해주세요."),
    POST_USERS_INVALID_NAME(false, 2020, "닉네임은 20자 이내로 입력해주세요."),
    POST_STORES_INVALID(false, 2021, "게시물은 50자 이내로 작성해주세요"),
    GET_REVIEW_TYPE_EMPTY(false, 2022, "종류는 한가지 이상 선택해주세요."),
    POST_REVIEW_INVALID_RATETYPE(false, 2023, "종류를 다시 확인해주세요."),
    POST_REVIEW_INVALID_CONTENT(false, 2024, "게시물은 1000자 이내로 작성해주세요."),
    GET_MENTION_INVALID(false, 2025, "리뷰를 쓴 사람과 댓글을 남긴 사람만 언급할 수 있습니다."),
    PATCH_USERS_INVALID_NAME(false, 2026, "이름은 2자 이상이어야 합니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),

    GET_ITEM_EMPTY(false, 3015, "존재하지 않는 식당입니다."),
    GET_USER_INVALID(false, 3016, "존재하지 않는 사용자입니다."),
    GET_REVIEW_EMPTY(false, 3017, "존재하지 않는 리뷰입니다."),


    PATCH_USER_INVALID_STATUS(false, 3018, "삭제할 권한이 없는 사용자입니다."),
    PATCH_USER_INVALID(false, 3019, "수정할 권한이 없는 사용자입니다."),

    GET_STORES_SEARCH_EMPTY(false, 3020, "등록된 식당이 없어요."),
    GET_WENT_EMPTY(false, 3021, "존재하지 않는 가고싶다입니다."),
    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

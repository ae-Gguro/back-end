package com.example.gguro.apiPayload.code.status;

import com.example.gguro.apiPayload.code.BaseErrorCode;
import com.example.gguro.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 멤버 관려 에러
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),

    // 예시,,,
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다."),

    // TEMP 관련 에러 (테스트)
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트 !"),

    // Apple OAuth 관련 에러
    APPLE_PUBLIC_KEY_RETRIEVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "APPLE5001", "Apple 공개키를 가져오는 데 실패했습니다."),
    APPLE_ID_TOKEN_PARSE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "APPLE5002", "ID 토큰 파싱에 실패했습니다."),
    APPLE_AUTH_CODE_INVALID(HttpStatus.BAD_REQUEST, "APPLE4001", "잘못된 authorization code입니다."),
    APPLE_ID_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "APPLE4002", "Apple로부터 유효한 ID 토큰을 받지 못했습니다."),
    APPLE_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "APPLE4003", "Apple 로그인에 실패했습니다."),
    APPLE_PRIVATE_KEY_PARSE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "APPLE5003", "Apple 비공개 키 파싱에 실패했습니다."),
    APPLE_CLIENT_SECRET_GENERATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "APPLE5004", "Apple Client Secret 생성에 실패했습니다."),

    // User 관련 에러
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4001", "유저가 이미 존재합니다."),
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER4002", "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4003", "유저가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER4004", "비밀번호가 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "USER4005", "유효하지 않은 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "USER4006", "인증이 필요한 요청입니다.");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
package com.Sucat.global.common.code;

import com.Sucat.global.common.dto.ReasonDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.Sucat.global.common.constant.ConstraintConstants.MAX_PASSWORD_LENGTH;
import static com.Sucat.global.common.constant.ConstraintConstants.MIN_PASSWORD_LENGTH;
import static org.springframework.http.HttpStatus.*;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode{

    /**
     * 전역 에러
     */
    _INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR,"500", "서버에서 요청을 처리 하는 동안 오류가 발생했습니다."),
    _BAD_REQUEST(BAD_REQUEST,"400", "입력 값이 잘못된 요청입니다."),
    _INVALID_TYPE_VALUE(BAD_REQUEST, "400", "입력 타입이 잘못된 요청입니다."),
    _UNAUTHORIZED(UNAUTHORIZED,"401", "인증이 필요합니다."),
    _FORBIDDEN(FORBIDDEN, "403", "금지된 요청입니다."),

    /**
     * Auth
     */
    USER_ALREADY_EXISTS(BAD_REQUEST, "400", "이미 가입된 유저입니다."),
    NEED_TO_JOIN(BAD_REQUEST, "400", "회원가입이 필요한 요청입니다."),
    USER_MISMATCH(UNAUTHORIZED, "401", "회원 정보가 불일치합니다."),

    /**
     * User
     */
    NICKNAME_DUPLICATION(CONFLICT, "409", "중복되는 닉네임입니다."),
    USER_NOT_FOUND(NOT_FOUND, "404", "존재하지 않는 회원입니다."),
    INVALID_INPUT_ID_PASSWORD(BAD_REQUEST, "400", "Email 또는 Password가 일치하지 않습니다."),
    SOCIAL_TYPE_ERROR(BAD_REQUEST,"400","소셜 타입 검증에 실패했습니다."),
    TERMS_NOT_ACCEPTED(BAD_REQUEST, "400", "약관에 동의하지 않았습니다."),

    /**
     * Password
     */
    PASSWORD_MISSING_OR_EMPTY(BAD_REQUEST, "400", "비밀번호는 null이나 빈 문자열일 수 없습니다."),
    PASSWORD_LENGTH_INVALID(BAD_REQUEST, "400", "비밀번호는 최소 " + MIN_PASSWORD_LENGTH + "자에서 최대 " + MAX_PASSWORD_LENGTH + "자여야 합니다."),
    PASSWORD_COMPLEXITY_REQUIRED(BAD_REQUEST, "400", "비밀번호는 숫자, 문자, 특수문자를 모두 포함해야 합니다."),
    PASSWORD_MISMATCH(BAD_REQUEST, "400", "입력한 비밀번호가 기존 비밀번호와 일치하지 않습니다."),

    /**
     * Token
     */
    INVALID_TOKEN(UNAUTHORIZED, "401", "유효하지 않은 토큰입니다."),
    INVALID_ACCESS_TOKEN(UNAUTHORIZED, "401", "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "401", "유효하지 않은 리프레쉬 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "404", "해당 유저 ID의 리프레쉬 토큰이 없습니다."),

    /**
     * Email
     */
    INVALID_VERIFICATION_CODE(BAD_REQUEST, "400", "유효하지 않은 인증 코드입니다."),
    INVALID_VERIFICATION_EMAIL(BAD_REQUEST, "400", "유효하지 않은 이메일입니다."),
    VERIFICATION_CODE_EXPIRED(BAD_REQUEST, "400", "인증 코드가 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
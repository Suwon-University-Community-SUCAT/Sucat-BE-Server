package com.Sucat.global.error;


import com.Sucat.domain.token.exception.TokenException;
import com.Sucat.domain.user.exception.UserException;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.common.response.ApiResponse;
import com.Sucat.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // enum type 일치하지 않아 binding 못할 경우 발생
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatchException", e);
        final ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<ApiResponse<ErrorCode>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("handleAccessDeniedException", e);
        return ApiResponse.onFailure(ErrorCode._FORBIDDEN);
    }

    // Required header가 없는 경우 발생
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleMissingHeaderException(MissingRequestHeaderException ex) {
        String errorMessage = "Required header '" + ex.getHeaderName() + "' is missing";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }

    // BusinessException 발생 시 처리
    @ExceptionHandler(BusinessException.class)
//    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<ApiResponse<ErrorCode>> handleBusinessException(final BusinessException e) {
        log.error("handleBusinessException", e);
        ErrorCode errorCode = e.getErrorCode();
        return ApiResponse.onFailure(errorCode);
    }

    /**
     * Token
     */
    @ExceptionHandler(TokenException.class)
    protected ResponseEntity<ApiResponse<ErrorCode>> handleTokenException(final TokenException e) {
        log.error("handleTokenException", e);
        ErrorCode errorCode = e.getErrorCode();
        return ApiResponse.onFailure(errorCode);
    }

    /**
     * User
     */
    @ExceptionHandler(UserException.class)
    protected ResponseEntity<ApiResponse<ErrorCode>> handleUserException(final UserException e) {
        log.error("handleTokenException", e);
        ErrorCode errorCode = e.getErrorCode();
        return ApiResponse.onFailure(errorCode);
    }
}


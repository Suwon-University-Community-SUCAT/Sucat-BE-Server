package com.Sucat.global.common.response;

import com.Sucat.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;


@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    @JsonProperty("is_success")
    private final Boolean isSuccess;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T payload;

    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(ErrorCode code, T payload) {
        ApiResponse<T> response = new ApiResponse<>(true, code.getCode(), code.getMessage(), payload);
        return ResponseEntity.status(code.getStatus()).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(ErrorCode code) {
        ApiResponse<T> response = new ApiResponse<>(true, code.getCode(), code.getMessage(), null);
        return ResponseEntity.status(code.getStatus()).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(String message) {
        ApiResponse<T> response = new ApiResponse<>(true, "200", message, null);
        return ResponseEntity.status(200).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> onSuccess() {
        ApiResponse<T> response = new ApiResponse<>(true, "200", "API 요청 성공", null);
        return ResponseEntity.status(200).body(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> onFailure(ErrorCode code) {
        ApiResponse<T> response = new ApiResponse<>(false, code.getCode(), code.getMessage(), null);
        return ResponseEntity.status(code.getStatus()).body(response);
    }
}

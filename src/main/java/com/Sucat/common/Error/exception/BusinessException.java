package com.Sucat.common.Error.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private com.lawProject.SSL.common.Error.exception.ErrorCode errorCode;

    public BusinessException(com.lawProject.SSL.common.Error.exception.ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(String message, com.lawProject.SSL.common.Error.exception.ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

package com.Sucat.global.error.exception;

import com.Sucat.global.common.code.ErrorCode;

import static com.Sucat.global.common.code.ErrorCode.*;

public class InvalidValueException extends BusinessException{

    public InvalidValueException(String value) {
        super(value, INVALID_INPUT_VALUE);
    }

    public InvalidValueException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }

}
package com.Sucat.global.error.exception;

import com.Sucat.global.error.ErrorCode;

import static com.Sucat.global.error.ErrorCode.*;

public class InvalidValueException extends BusinessException{

    public InvalidValueException(String value) {
        super(value, INVALID_INPUT_VALUE);
    }

    public InvalidValueException(String value, ErrorCode errorCode) {
        super(value, errorCode);
    }

}
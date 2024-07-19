package com.Sucat.global.infra.email.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class EmailException extends BusinessException {
    public EmailException(ErrorCode code) {
        super(code);
    }
}


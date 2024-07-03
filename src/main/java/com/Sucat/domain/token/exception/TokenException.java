package com.Sucat.domain.token.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class TokenException extends BusinessException {
    public TokenException(ErrorCode code) {
        super(code);
    }
}

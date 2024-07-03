package com.Sucat.domain.user.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class UserException extends BusinessException {
    public UserException(ErrorCode code) {
        super(code);
    }
}

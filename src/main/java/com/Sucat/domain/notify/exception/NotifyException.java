package com.Sucat.domain.notify.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class NotifyException extends BusinessException {
    public NotifyException(ErrorCode errorCode) {
        super(errorCode);
    }
}

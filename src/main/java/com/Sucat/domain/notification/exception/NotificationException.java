package com.Sucat.domain.notification.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class NotificationException extends BusinessException {
    public NotificationException(ErrorCode code) {
        super(code);
    }
}

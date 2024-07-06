package com.Sucat.domain.image.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class ImageException extends BusinessException {
    public ImageException(ErrorCode code) {
        super(code);
    }
}
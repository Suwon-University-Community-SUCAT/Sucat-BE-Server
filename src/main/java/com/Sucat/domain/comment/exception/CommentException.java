package com.Sucat.domain.comment.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class CommentException extends BusinessException {
    public CommentException(ErrorCode code) {
        super(code);
    }
}

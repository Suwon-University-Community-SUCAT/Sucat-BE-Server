package com.Sucat.domain.board.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class BoardException extends BusinessException {
    public BoardException(ErrorCode code) {
        super(code);
    }
}

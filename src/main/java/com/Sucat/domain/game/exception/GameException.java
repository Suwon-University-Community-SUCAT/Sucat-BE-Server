package com.Sucat.domain.game.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class GameException extends BusinessException {
    public GameException(ErrorCode code) {
        super(code);
    }
}

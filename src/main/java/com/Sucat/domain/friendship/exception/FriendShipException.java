package com.Sucat.domain.friendship.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;

public class FriendShipException extends BusinessException {
    public FriendShipException(ErrorCode code) {
        super(code);
    }
}
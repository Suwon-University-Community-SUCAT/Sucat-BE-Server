package com.Sucat.domain.chatroom.exception;

import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.error.exception.BusinessException;
public class ChatRoomException extends BusinessException {
    public ChatRoomException(ErrorCode code) {
        super(code);
    }
}
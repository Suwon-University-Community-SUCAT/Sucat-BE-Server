package com.Sucat.global.error.exception;

import com.Sucat.global.error.ErrorCode;

import static com.Sucat.global.error.ErrorCode.*;

public class EntityNotFoundException extends BusinessException{

    public EntityNotFoundException(String message) {
        super(message, ENTITY_NOT_FOUND);
    }
}

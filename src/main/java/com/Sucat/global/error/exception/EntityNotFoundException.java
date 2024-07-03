package com.Sucat.global.error.exception;

import static com.Sucat.global.common.code.ErrorCode.*;

public class EntityNotFoundException extends BusinessException{

    public EntityNotFoundException(String message) {
        super(message, ENTITY_NOT_FOUND);
    }
}

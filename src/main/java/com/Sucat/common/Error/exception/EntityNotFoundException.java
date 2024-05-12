package com.Sucat.common.Error.exception;

public class EntityNotFoundException extends BusinessException{

    public EntityNotFoundException(String message) {
        super(message, com.lawProject.SSL.common.Error.exception.ErrorCode.ENTITY_NOT_FOUND);
    }
}

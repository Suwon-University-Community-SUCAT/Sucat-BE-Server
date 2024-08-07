package com.Sucat.domain.notify.dto;

public enum NotifyMessage {
    NEW_REQUEST("새로운 요청이 있습니다.");

    private String message;

    NotifyMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}

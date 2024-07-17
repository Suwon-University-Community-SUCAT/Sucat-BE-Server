package com.Sucat.domain.chatmessage.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MessageResponse {
    private Long messageId;
    private String content;
    private LocalDateTime sendTime;
}

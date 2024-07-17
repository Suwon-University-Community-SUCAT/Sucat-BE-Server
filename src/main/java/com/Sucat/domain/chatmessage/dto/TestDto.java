package com.Sucat.domain.chatmessage.dto;

import com.Sucat.domain.chatmessage.model.ChatMessage;
import com.Sucat.global.common.response.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class TestDto {
    List<ChatMessage> messageList;
    PageInfo pageInfo;
}

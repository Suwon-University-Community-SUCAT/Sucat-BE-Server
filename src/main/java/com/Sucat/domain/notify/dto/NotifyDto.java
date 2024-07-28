package com.Sucat.domain.notify.dto;

import com.Sucat.domain.notify.model.Notify;
import lombok.Builder;

public class NotifyDto {

    /**
     * Response
     */
    @Builder
    public record CreateNotifyResponse(
            Long id,
            String name,
            String content,
            String type,
            String createAt
    ) {
        public static CreateNotifyResponse of(Notify notify) {
            return CreateNotifyResponse.builder()
                    .content(notify.getContent())
                    .id(notify.getId())
                    .name(notify.getUser().getName())
                    .createAt(notify.getCreatedAt().toString())
                    .build();
        }
    }
}

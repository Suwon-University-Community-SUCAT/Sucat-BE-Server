package com.Sucat.domain.notify.dto;

import com.Sucat.domain.notify.model.Notify;
import com.Sucat.domain.notify.model.NotifyType;
import lombok.Builder;

import java.time.LocalDateTime;

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

    @Builder
    public record FindNotifyResponse(
            Long id,
            String content,
            String url,
            boolean isRead,
            NotifyType notifyType,
            LocalDateTime createAt
    ) {
        public static FindNotifyResponse of(Notify notify) {
            return FindNotifyResponse.builder()
                    .id(notify.getId())
                    .content(notify.getContent())
                    .url(notify.getUrl())
                    .isRead(notify.getIsRead())
                    .notifyType(notify.getNotifyType())
                    .createAt(notify.getCreatedAt())
                    .build();
        }
    }
}


package com.Sucat.domain.notify.dto;

import com.Sucat.domain.notify.model.Notify;
import com.Sucat.domain.notify.model.NotifyType;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

public class NotifyDto {
    /**
     * Request
     */
    public record ReadNotifyRequest(
            @Positive // 양수인지 검증
            Long notifyId
    ) {

    }

    /**
     * Response
     */
    @Builder
    public record CreateNotifyResponse(
            Long id,
            String name,
            String content,
            String type,
            String createdAt
    ) {
        public static CreateNotifyResponse of(Notify notify) {
            return CreateNotifyResponse.builder()
                    .content(notify.getContent())
                    .id(notify.getId())
                    .name(notify.getUser().getName())
                    .createdAt(notify.getCreatedAt().toString())
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
            LocalDateTime createdAt
    ) {
        public static FindNotifyResponse of(Notify notify) {
            return FindNotifyResponse.builder()
                    .id(notify.getId())
                    .content(notify.getContent())
                    .url(notify.getUrl())
                    .isRead(notify.getIsRead())
                    .notifyType(notify.getNotifyType())
                    .createdAt(notify.getCreatedAt())
                    .build();
        }
    }
}


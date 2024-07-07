package com.Sucat.domain.notification.dto;

import com.Sucat.domain.notification.model.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

import static com.Sucat.global.common.constant.ConstraintConstants.*;

public class NotificationDto {

    /**
     * Request
     */
    public record CreateNotificationRequest(
            @NotNull
            String title,
            String content
    ) {
        public Notification toEntity() {
            return Notification.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    /**
     * Response
     */
    @Builder
    public record SystemListResponse(
            String title,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT_YYYY_MM_DD_HH_MM)
            LocalDateTime createTime
    ) {
        public static SystemListResponse of(Notification notification) {
            return SystemListResponse.builder()
                    .title(notification.getTitle())
                    .createTime(notification.getCreatedAt())
                    .build();
        }
    }
}

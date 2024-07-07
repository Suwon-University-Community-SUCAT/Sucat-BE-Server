package com.Sucat.domain.notification.dto;

import com.Sucat.domain.notification.model.Notification;
import jakarta.validation.constraints.NotNull;

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
}

package com.Sucat.domain.notification.dto;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.notification.model.Notification;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.Sucat.global.common.constant.ConstraintConstants.TIME_FORMAT_YYYY_MM_DD_HH_MM;

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
            @NotNull
            Long notificationId,
            @NotNull
            String title,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT_YYYY_MM_DD_HH_MM)
            LocalDateTime createTime
    ) {
        public static SystemListResponse of(Notification notification) {
            return SystemListResponse.builder()
                    .notificationId(notification.getId())
                    .title(notification.getTitle())
                    .createTime(notification.getCreatedAt())
                    .build();
        }
    }

    @Builder
    public record SystemListWithSizeResponse(
            List<SystemListResponse> systemList,
            int listSize
    ) {
        public static SystemListWithSizeResponse of(List<SystemListResponse> systemList, int listSize) {
            return SystemListWithSizeResponse.builder()
                    .systemList(systemList)
                    .listSize(listSize)
                    .build();
        }
    }

    @Builder
    public record NotificationDetailResponse(
            @NotNull
            String title,
            String content,
            List<String> imageNames,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT_YYYY_MM_DD_HH_MM)
            LocalDateTime createTime
    ) {
        public static NotificationDetailResponse of(Notification notification) {
            return NotificationDetailResponse.builder()
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .imageNames(notification.getImages().stream().map(Image::getImageName).toList())
                    .createTime(notification.getCreatedAt())
                    .build();
        }
    }
}


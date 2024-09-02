package com.Sucat.domain.notification.controller;

import com.Sucat.domain.notification.service.NotificationService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.Sucat.domain.notification.dto.NotificationDto.*;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /* 공지사항 작성 */
    @PostMapping("/admin/notification")
    public ResponseEntity<ApiResponse<Object>> createNotification(
            @RequestPart(name = "notificationRequest") @Valid CreateNotificationRequest request,
            @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        notificationService.create(request.toEntity(), images);

        return ApiResponse.onSuccess(SuccessCode._CREATED);
    }

    @GetMapping("/notification/system")
    public ResponseEntity<ApiResponse<Object>> getNotificationList(
            @PageableDefault(page = 0, size = 10) @Nullable final Pageable pageable) {
        SystemListWithSizeResponse systemListWithSizeResponse = notificationService.getSystemList(pageable);

        return ApiResponse.onSuccess(SuccessCode._OK, systemListWithSizeResponse);
    }

    @GetMapping("/notification/{notificationId}")
    public ResponseEntity<ApiResponse<Object>> getNotificationDetail(@PathVariable(name = "notificationId") Long notificationId) {
        NotificationDetailResponse notificationDetail = notificationService.getNotificationDetail(notificationId);

        return ApiResponse.onSuccess(SuccessCode._OK, notificationDetail);
    }
}

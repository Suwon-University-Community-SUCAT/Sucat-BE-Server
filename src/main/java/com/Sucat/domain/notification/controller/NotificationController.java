package com.Sucat.domain.notification.controller;

import com.Sucat.domain.notification.service.NotificationService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.Sucat.domain.notification.dto.NotificationDto.CreateNotificationRequest;
import static com.Sucat.domain.notification.dto.NotificationDto.SystemListResponse;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /* 공지사항 작성 */
    @PostMapping("/admin/notification")
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestPart(name = "notificationRequest") @Valid CreateNotificationRequest createNotificationRequest,
            @RequestPart(name = "images", required = false) List<MultipartFile> images) throws IOException {
        notificationService.create(createNotificationRequest.toEntity(), images);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    @GetMapping("/notification/system")
    public ResponseEntity<ApiResponse<Object>> List(
            @PageableDefault(page = 0, size = 10) @Nullable final Pageable pageable
    ) {
        List<SystemListResponse> systemList = notificationService.getSystemList(pageable);

        return ApiResponse.onSuccess(SuccessCode._OK, systemList);
    }
}

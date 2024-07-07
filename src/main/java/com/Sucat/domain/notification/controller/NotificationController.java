package com.Sucat.domain.notification.controller;

import com.Sucat.domain.notification.service.NotificationService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.Sucat.domain.notification.dto.NotificationDto.CreateNotificationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class NotificationController {
    private final NotificationService notificationService;

    /* 공지사항 작성 */
    @PostMapping("/notification")
    public ResponseEntity<ApiResponse<Object>> signup(
            @RequestPart(name = "notificationRequest") @Valid CreateNotificationRequest createNotificationRequest,
            @RequestPart(name = "image", required = false) List<MultipartFile> images) throws IOException {
        notificationService.create(createNotificationRequest.toEntity(), images);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}

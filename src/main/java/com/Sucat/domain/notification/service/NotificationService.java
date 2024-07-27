package com.Sucat.domain.notification.service;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.image.service.ImageService;
import com.Sucat.domain.notification.exception.NotificationException;
import com.Sucat.domain.notification.model.Notification;
import com.Sucat.domain.notification.repository.NotificationQueryRepository;
import com.Sucat.domain.notification.repository.NotificationRepository;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.Sucat.domain.notification.dto.NotificationDto.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;

    public Notification getNotification(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    @Transactional
    public void create(Notification notification, List<MultipartFile> images) throws IOException {
        notificationRepository.save(notification);

        if (images != null && !images.isEmpty()) {
            imageService.storeFiles(images).forEach(imageName -> {
                Image image = Image.ofNotification(notification, imageName);
                notification.addNotificationImage(image);
            });
        }
    }

    public SystemListWithSizeResponse getSystemList(Pageable pageable) {
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        List<SystemListResponse> systemList = notificationRepository.findAll(pageRequest).stream()
                .map(SystemListResponse::of)
                .toList();
        int listSize = systemList.size();
        return SystemListWithSizeResponse.of(systemList, listSize);
    }

    public NotificationDetailResponse getNotificationDetail(Long notificationId) {
        Notification notification = notificationQueryRepository.findNotificationById(notificationId);
        return NotificationDetailResponse.of(notification);
    }
}

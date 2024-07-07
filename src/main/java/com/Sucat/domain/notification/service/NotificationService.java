package com.Sucat.domain.notification.service;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.image.service.ImageService;
import com.Sucat.domain.notification.model.Notification;
import com.Sucat.domain.notification.repository.NotificationRepository;
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

import static com.Sucat.domain.notification.dto.NotificationDto.SystemListResponse;
import static com.Sucat.domain.notification.dto.NotificationDto.SystemListWithSizeResponse;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void create(Notification notification, List<MultipartFile> images) throws IOException {
        notificationRepository.save(notification);

        if (images != null && !images.isEmpty()) {
            imageService.storeFiles(images).forEach(imageUrl -> {
                Image image = Image.ofNotification(notification, imageUrl);
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
}

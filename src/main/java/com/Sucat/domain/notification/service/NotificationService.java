package com.Sucat.domain.notification.service;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.image.service.S3Uploader;
import com.Sucat.domain.notification.exception.NotificationException;
import com.Sucat.domain.notification.model.Notification;
import com.Sucat.domain.notification.repository.NotificationQueryRepository;
import com.Sucat.domain.notification.repository.NotificationRepository;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.Sucat.domain.notification.dto.NotificationDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationQueryRepository notificationQueryRepository;
    private final S3Uploader s3Uploader;

    /* 공지사항 작성 메서드 */
    @Transactional
    public void create(Notification notification, List<MultipartFile> images) throws IOException {
        if (images != null && !images.isEmpty()) {
            List<Map<String, String>> imageInfos = s3Uploader.uploadMultiple(images);

            imageInfos.forEach(imageInfo ->
                    {
                        Image image = Image.ofNotification(notification, imageInfo.get("imageUrl"), imageInfo.get("imageName"));
                        notification.addNotificationImage(image);
                    }
            );
        }
        notificationRepository.save(notification);
    }

    /* 공지사항 목록 조회 메서드 */
    public SystemListWithSizeResponse getSystemList(Pageable pageable) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        List<SystemListResponse> systemList = notificationRepository.findAll(pageRequest).stream()
                .map(SystemListResponse::of)
                .toList();
        int listSize = systemList.size();
        return SystemListWithSizeResponse.of(systemList, listSize);
    }

    /* 공지사항 상세 조회 메서드 */
    public NotificationDetailResponse getNotificationDetail(Long notificationId) {
        Notification notification = notificationQueryRepository.findNotificationById(notificationId);
        return NotificationDetailResponse.of(notification);
    }

    /* Using Method */
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }
}

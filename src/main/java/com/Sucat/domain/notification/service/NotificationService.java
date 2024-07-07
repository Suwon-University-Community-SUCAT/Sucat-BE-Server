package com.Sucat.domain.notification.service;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.image.service.ImageService;
import com.Sucat.domain.notification.model.Notification;
import com.Sucat.domain.notification.repository.NotificationRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;

    public void create(Notification notification, HttpServletRequest request, MultipartFile image) throws IOException {
        User admin = jwtUtil.getUserFromRequest(request);
        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.storeFile(image, admin.getId());
            Image userImage = Image.ofUser(admin, imageUrl);
            admin.updateUserImage(userImage);
        }
    }
}

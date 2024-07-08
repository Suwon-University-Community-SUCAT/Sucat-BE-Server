package com.Sucat.domain.user.dto;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.notification.dto.NotificationDto;
import com.Sucat.domain.notification.model.Notification;
import com.Sucat.domain.user.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.Sucat.global.common.constant.ConstraintConstants.TIME_FORMAT_YYYY_MM_DD_HH_MM;

public class UserDto {

    /**
     * Request
     */
    public record UserNicknameRequest(
            @NotNull
            String nickname
    ) {
    }

    public record UserTermAgree(
            @NotNull
            List<Boolean> agreements
    ) {}

    @Builder
    public record JoinUserRequest(
            @Email
            @NotNull
            String email,
            @NotNull
            String password,
            String name,
            String nickname,
            String department
    ) {
        public User toEntity() {
            return User.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .nickname(nickname)
                    .department(department)
                    .build();
        }
    }

    public record PasswordResetRequest(
            @NotNull
            @Email
            String email,
            @NotNull
            String password
    ) {

    }

    public record UserProfileUpdateRequest(
            @NotNull
            @Size(min = 2, max = 20, message = "닉네임은 2자에서 20자 사이여야 합니다.")
            String nickname,
            String intro
    ) {

    }

    public record UserPasswordUpdateRequest(
            @NotNull
            String currentPassword,
            @NotNull
            String newPassword
    ) {

    }


    /**
     * Response
     */
    @Builder
    public record UserProfileResponse(
            @NotNull
            String nickname,
            String intro,
            String imageUrl
    ) {
        public static UserProfileResponse of(User user) {
            Image userImage = user.getUserImage();
            String imageUrl = (userImage != null) ? userImage.getImageUrl() : null;

            return UserProfileResponse.builder()
                    .nickname(user.getNickname())
                    .intro(user.getIntro())
                    .imageUrl(imageUrl)
                    .build();
        }
    }
}

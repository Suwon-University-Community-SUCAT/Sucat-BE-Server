package com.Sucat.domain.user.dto;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

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
            @Size(min = 2, max = 20, message = "닉네임은 2자에서 20자 사이여야 합니다.")
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

    @Builder
    public record ResponseOnlyUserNameWithId(
            Long userId,
            String nickname
            ) {
        public static ResponseOnlyUserNameWithId of(User user) {
            return ResponseOnlyUserNameWithId.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .build();
        }
    }
}

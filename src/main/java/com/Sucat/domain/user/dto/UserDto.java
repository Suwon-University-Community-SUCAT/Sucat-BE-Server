package com.Sucat.domain.user.dto;

import com.Sucat.domain.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class UserDto {

    /**
     * Request
     */

    public record UserEmailRequest(
            @Email
            @NotNull
            String email
            ) {
    }
    public record UserNicknameRequest(
            @NotNull
            String nickname
    ) {
    }

    @Builder

    public record JoinUserRequest(
            @Email
            @NotNull
            String email,
            @NotNull
            String password,
            String name,
            String nickname,
            String department,
            String socialNumber // 있어야 되나?
    ) {
        public User toEntity(String encodePassword) {
            return User.builder()
                    .email(email)
                    .password(encodePassword)
                    .name(name)
                    .nickname(nickname)
                    .department(department)
                    .build();
        }
    }

    public record LoginUserRequest(
            @Email
            @NotNull
            String email,
            @NotNull
            String password
    ) {
    }


    /**
     * Response
     */
}

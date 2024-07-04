package com.Sucat.domain.user.dto;

import com.Sucat.domain.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
            String nickname,
            String department
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

    public record PasswordResetRequest(
            @NotNull
            @Email
            String email,
            @NotNull
            String password
    ) {

    }


    /**
     * Response
     */
}

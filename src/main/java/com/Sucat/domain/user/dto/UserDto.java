package com.Sucat.domain.user.dto;

import com.Sucat.domain.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static com.Sucat.global.util.ConstraintConstants.*;

public class UserDto {

    /**
     * Request
     */

    public record UserEmailRequest(
            @Email
            @NotNull
            @Schema(description = USER_EMAIL)
            String email
            ) {
    }

    @Builder

    public record JoinUserRequest(
            @Email
            @NotNull
            @Schema(description = USER_EMAIL)
            String email,
            @NotNull
            @Schema(description = USER_PASSWORD)
            String password,
            @Schema(description = USER_NAME)
            String name,
            @Schema(description = USER_NICKNAME)
            String nickname,
            @Schema(description = USER_DEPARTMENT)
            String department,
            String socialNumber // 있어야 되나?
    ) {
        public User toEntity(String encodePassword) {
            return User.builder()
                    .email(email)
                    .password(encodePassword)
                    .name(name)
                    .nickName(nickname)
                    .department(department)
                    .build();
        }
    }

    /**
     * Response
     */
}

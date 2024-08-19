package com.Sucat.domain.comment.dto;

import jakarta.validation.constraints.NotNull;

public class CommentDto {
    /**
     * Request
     */
    public record CommentPostRequest(
            @NotNull
            String content
    ) {

    }

    /**
     * Response
     */
}

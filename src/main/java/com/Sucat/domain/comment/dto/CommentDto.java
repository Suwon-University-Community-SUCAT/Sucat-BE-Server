package com.Sucat.domain.comment.dto;

import com.Sucat.domain.comment.domain.Comment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

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
    @Builder
    public record CommentResponseWithBoard(
            @Positive
            Long commentId,
            String userNickname,
            String userProfileImageName,
            String content,
            int likeCount,
            int commentCount,
            LocalDateTime createAt
    ) {
        public static CommentResponseWithBoard of(Comment comment) {
            return CommentResponseWithBoard.builder()
                    .commentId(comment.getId())
                    .userNickname(comment.getUser().getNickname())
                    .userProfileImageName(comment.getUser().getUserImage().getImageName())
                    .content(comment.getContent())
                    .likeCount(comment.getLikeCount())
                    .commentCount(comment.getCommentCount())
                    .createAt(comment.getCreatedAt())
                    .build();
        }
    }
}

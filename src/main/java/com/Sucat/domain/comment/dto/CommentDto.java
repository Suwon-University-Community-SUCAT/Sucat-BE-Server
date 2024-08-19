package com.Sucat.domain.comment.dto;

import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.user.model.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Optional;

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
            User user = comment.getUser();
            Image userImage = user.getUserImage();

            return CommentResponseWithBoard.builder()
                    .commentId(comment.getId())
                    .userNickname(user.getNickname())
                    .userProfileImageName(Optional.ofNullable(userImage).map(Image::getImageName).orElse(null))
                    .content(comment.getContent())
                    .likeCount(comment.getLikeCount())
                    .commentCount(comment.getCommentCount())
                    .createAt(comment.getCreatedAt())
                    .build();
        }
    }
}

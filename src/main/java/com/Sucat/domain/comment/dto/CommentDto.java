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
            LocalDateTime createAt,
            boolean checkWriter, // 게시글 작성자가 단 댓글인지
            boolean isLikedByUser // 현재 로그인된 회원이 좋아요를 누른 댓글인지
    ) {
        public static CommentResponseWithBoard of(Comment comment, Long currentUserId) {
            User user = comment.getUser();
            Image userImage = user.getUserImage();

            boolean isLikedByUser = comment.getLikeList().stream()
                    .anyMatch(like -> like.getUser().getId().equals(currentUserId));

            return CommentResponseWithBoard.builder()
                    .commentId(comment.getId())
                    .userNickname(user.getNickname())
                    .userProfileImageName(Optional.ofNullable(userImage).map(Image::getImageName).orElse(null))
                    .content(comment.getContent())
                    .likeCount(comment.getLikeCount())
                    .commentCount(comment.getCommentCount())
                    .createAt(comment.getCreatedAt())
                    .checkWriter(comment.isCheckWriter())
                    .isLikedByUser(isLikedByUser)
                    .build();
        }
    }
}

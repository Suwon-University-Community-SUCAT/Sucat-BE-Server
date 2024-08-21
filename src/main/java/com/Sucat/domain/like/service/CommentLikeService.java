package com.Sucat.domain.like.service;

import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.comment.service.CommentService;
import com.Sucat.domain.like.model.CommentLike;
import com.Sucat.domain.like.repository.CommentLikeRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final UserService userService;
    private final CommentService commentService;

    @Transactional
    public void like(Long commendId, HttpServletRequest request) {
        User user = userService.getUserInfo(request);
        Comment comment = commentService.findById(commendId);

        // 이미 좋아요 누른 경우 확인
        CommentLike existingCommentLike = commentLikeRepository.findByUserAndComment(user, comment);

        if (existingCommentLike != null) {
            // 이미 좋아요 누른 경우: 좋아요 취소 (삭제)
            log.info("식별자(commentId): {}, 이미 좋아요 한 게시물 -> 좋아요 삭제", commendId);
            commentLikeRepository.delete(existingCommentLike);
            comment.decrementLikeCount();
        } else {
            // 좋아요 누르지 않은 경우: 좋아요 추가
            log.info("식별자(commentId): {}, 게시글에 좋아요를 누릅니다.", commendId);

            CommentLike commentLike = CommentLike.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(commentLike);
            comment.addLike(commentLike);
        }
    }
}

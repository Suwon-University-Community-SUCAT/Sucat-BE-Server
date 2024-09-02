package com.Sucat.domain.like.controller;

import com.Sucat.domain.comment.service.CommentService;
import com.Sucat.domain.like.service.CommentLikeService;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.annotation.CurrentUser;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments/like")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;
    private final CommentService commentService;

    /* 댓글 좋아요/취소하기 */
    @PostMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Object>> like(
            @PathVariable Long commentId,
            @CurrentUser User user
            ) {
        commentLikeService.like(commentService.findById(commentId), user);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}

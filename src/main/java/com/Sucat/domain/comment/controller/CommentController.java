package com.Sucat.domain.comment.controller;

import com.Sucat.domain.comment.service.CommentService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.Sucat.domain.comment.dto.CommentDto.CommentPostRequest;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /* 댓글 생성 */
    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Object>> write(
            @PathVariable Long boardId,
            HttpServletRequest request,
            @RequestBody CommentPostRequest commentPostDTO) {
        commentService.write(boardId, request, commentPostDTO);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 댓글 삭제 */
    @DeleteMapping("/commentId")
    public ResponseEntity<ApiResponse<Object>> delete(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        commentService.delete(commentId, request);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}

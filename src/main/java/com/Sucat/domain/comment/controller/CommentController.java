package com.Sucat.domain.comment.controller;

import com.Sucat.domain.comment.service.CommentService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.MyBoardResponse;
import static com.Sucat.domain.comment.dto.CommentDto.CommentPostRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

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

    /* 내가 쓴 댓글 작성한 게시글 조회 */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Object>> myComment(HttpServletRequest request) {
        List<MyBoardResponse> myBoardResponses = commentService.myComment(request);

        return ApiResponse.onSuccess(SuccessCode._OK, myBoardResponses);
    }
}

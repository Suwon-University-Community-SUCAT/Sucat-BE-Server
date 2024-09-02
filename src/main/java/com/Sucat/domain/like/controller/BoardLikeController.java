package com.Sucat.domain.like.controller;

import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.like.service.BoardLikeService;
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
@RequestMapping("/api/v1/boards/like")
public class BoardLikeController {
    private final BoardLikeService boardLikeService;
    private final BoardService boardService;

    /* 게시물 좋아요/취소하기 */
    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Object>> like(
            @PathVariable Long boardId,
            @CurrentUser User user
    ) {
        boardLikeService.like(boardService.findBoardById(boardId), user);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}

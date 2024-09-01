package com.Sucat.domain.comment.controller;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.comment.service.CommentService;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.service.NotifyService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.annotation.CurrentUser;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.MyBoardResponse;
import static com.Sucat.domain.comment.dto.CommentDto.CommentPostRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;
    private final UserService userService;
    private final NotifyService notifyService;

    /* 댓글 생성 */
    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Object>> write(
            @PathVariable Long boardId,
            @CurrentUser User user,
            @RequestBody CommentPostRequest commentPostDTO)
    {
        Board board = boardService.findBoardById(boardId);
        String content = commentPostDTO.content();
        commentService.write(board, user, content);

        notifyService.send(board.getUser(), NotifyType.POST_COMMENT, "새로운 댓글이 달렸습니다: " + content, "/api/v1/boards/"+boardId);
        log.info("게시물 작성자에게 알림이 전송됩니다. 게시글 식별자: {}", boardId);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 댓글 삭제 */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Object>> delete(
            @PathVariable Long commentId,
            @CurrentUser User user
    ) {
        commentService.delete(commentId, user);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 내가 댓글 작성한 게시글 조회 */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Object>> myComment(@CurrentUser User user) {
        List<MyBoardResponse> myBoardResponses = commentService.myComment(user);

        return ApiResponse.onSuccess(SuccessCode._OK, myBoardResponses);
    }
}

package com.Sucat.domain.comment.controller;

import com.Sucat.domain.comment.dto.CommentPostDTO;
import com.Sucat.domain.comment.dto.CommentPostResponse;
import com.Sucat.domain.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /* 댓글 생성 */
    @PostMapping("/{boardId}/{userId}")
    public ResponseEntity<CommentPostResponse> createComment(
            @PathVariable Long boardId,
            @PathVariable Long userId,
            @RequestBody CommentPostDTO commentPostDTO) {

        /* 댓글 생성 로직 */
        CommentPostResponse response = commentService.createComment(boardId, userId, commentPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}

package com.Sucat.domain.comment.service;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.comment.repository.CommentRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.Sucat.domain.comment.dto.CommentDto.CommentPostRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardService boardService;
    private final UserService userService;

    /* 댓글 작성 메서드 */
    public void write(Long boardId, HttpServletRequest request, CommentPostRequest commentPostDTO) {
        Board board = boardService.findBoardById(boardId);
        User user = userService.getUserInfo(request);


        Comment comment = Comment.builder()
                .content(commentPostDTO.content())
                .board(board)
                .user(user)
                .build();

        board.addComment(comment);
        user.addComment(comment);
    }

    /* 댓글 삭제 메서드 */
}

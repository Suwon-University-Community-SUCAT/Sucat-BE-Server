package com.Sucat.domain.comment.service;

import com.Sucat.domain.board.exception.BoardException;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.comment.exception.CommentException;
import com.Sucat.domain.comment.repository.CommentRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.ErrorCode;
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

        commentRepository.save(comment);
        log.info("식별자: {}, 댓글 작성", comment.getId());
        board.addComment(comment);
        user.addComment(comment);
    }

    /* 댓글 삭제 메서드 */
    public void delete(Long commentId, HttpServletRequest request) {
        Comment comment = findById(commentId);
        validateUserAuthorization(request, comment);

        commentRepository.deleteById(commentId);
        log.info("식별자: {}, 댓글 삭제 완료", commentId);
    }

    /* Using Method */
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateUserAuthorization(HttpServletRequest request, Comment comment) {
        User user = userService.getUserInfo(request);

        // 게시글 작성자만 수정 가능
        if (!comment.getUser().equals(user)) {
            log.info("error: 댓글 작성자가 아닌 사용자의 접근");
            throw new BoardException(ErrorCode._UNAUTHORIZED_USER);
        }
    }
}

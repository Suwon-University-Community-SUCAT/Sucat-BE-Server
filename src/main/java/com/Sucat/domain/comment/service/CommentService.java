package com.Sucat.domain.comment.service;

import com.Sucat.domain.board.exception.BoardException;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.comment.exception.CommentException;
import com.Sucat.domain.comment.repository.CommentRepository;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.service.NotifyService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.MyBoardResponse;
import static com.Sucat.domain.comment.dto.CommentDto.CommentPostRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardService boardService;
    private final UserService userService;
    private final NotifyService notifyService;

    /* 댓글 작성 메서드 */
    @Transactional
    public void write(Long boardId, HttpServletRequest request, CommentPostRequest commentPostDTO) {
        Board board = boardService.findBoardById(boardId);
        User user = userService.getUserInfo(request);
        boolean checkWriter = false;
        if (board.getUser().equals(user)) {
            checkWriter = true;
        }
        String content = commentPostDTO.content();

        Comment comment = Comment.builder()
                .content(content)
                .checkWriter(checkWriter)
                .build();

        commentRepository.save(comment);
        log.info("식별자: {}, 댓글 작성", comment.getId());
        board.addComment(comment);
        user.addComment(comment);

        log.info("게시물 작성자에게 알림이 전송됩니다. 게시글 식별자: {}", boardId);
        notifyService.send(board.getUser(), NotifyType.POST_COMMENT, "새로운 댓글이 달렸습니다: " + content, "/api/v1/boards/"+boardId); // 알림 클릭시 댓글이 달린 게시물로 이동
    }

    /* 댓글 삭제 메서드 */
    @Transactional
    public void delete(Long commentId, HttpServletRequest request) {
        Comment comment = findById(commentId);
        validateUserAuthorization(request, comment);

        comment.getBoard().decrementCommentCount();
        commentRepository.deleteById(commentId);
        log.info("식별자: {}, 댓글 삭제 완료", commentId);
    }

    /* 나의 댓글 작성한 게시글 조회 메서드 */
    public List<MyBoardResponse> myComment(HttpServletRequest request) {
        User user = userService.getUserInfo(request);
        log.info("식별자: {}, 해당 유저가 작성한 게시글을 조회합니다.", user.getId());
        return user.getCommentList().stream()
                .map(comment -> MyBoardResponse.of(comment.getBoard()))
                .toList();
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

package com.Sucat.domain.comment.service;

import com.Sucat.domain.board.exception.BoardException;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.comment.dto.CommentDto;
import com.Sucat.domain.comment.exception.CommentException;
import com.Sucat.domain.comment.repository.CommentRepository;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.service.NotifyService;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.MyBoardResponse;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardService boardService;
    private final NotifyService notifyService;

    /* 댓글 작성 메서드 */
    @Transactional
    public void write(Long boardId, User user, CommentDto.CommentPostRequest request) {
        Board board = boardService.findBoardById(boardId);
        String content = request.content();

        boolean checkWriter = board.getUser().equals(user);

        Comment comment = Comment.builder()
                .content(content)
                .checkWriter(checkWriter)
                .build();

        commentRepository.save(comment);
        board.addComment(comment);
        user.addComment(comment);

        log.info("식별자: {}, 댓글 작성", comment.getId());

        // 알림 서비스 호출
        notifyService.send(board.getUser(), NotifyType.POST_COMMENT, "새로운 댓글이 달렸습니다: " + content, "/api/v1/boards/"+boardId);
        log.info("게시물 작성자에게 알림이 전송됩니다. 게시글 식별자: {}", boardId);
    }

    /* 댓글 삭제 메서드 */
    @Transactional
    public void delete(Long commentId, User user) {
        Comment comment = findById(commentId);
        validateUserAuthorization(user, comment);

        comment.getBoard().removeComment(comment);
        comment.getUser().removeComment(comment);

        commentRepository.deleteById(commentId);
        log.info("식별자: {}, 댓글 삭제 완료", commentId);
    }

    /* 나의 댓글 작성한 게시글 조회 메서드 */
    public List<MyBoardResponse> myComment(User user) {
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

    private void validateUserAuthorization(User user, Comment comment) {
        // 게시글 작성자만 수정 가능
        if (!comment.getUser().equals(user)) {
            log.info("댓글 작성자가 아닌 사용자의 올바르지 않은 접근, user ID: {}, comment ID: {}", user.getId(), comment.getId());
            throw new BoardException(ErrorCode._UNAUTHORIZED_USER);
        }
    }
}

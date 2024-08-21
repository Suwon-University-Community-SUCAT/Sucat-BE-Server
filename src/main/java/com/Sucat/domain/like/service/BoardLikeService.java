package com.Sucat.domain.like.service;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.like.model.BoardLike;
import com.Sucat.domain.like.repository.BoardLikeRepository;
import com.Sucat.domain.scrap.model.Scrap;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardLikeService {
    private final BoardLikeRepository boardLikeRepository;
    private final UserService userService;
    private final BoardService boardService;

    @Transactional
    public void like(Long boardId, HttpServletRequest request) {
        User user = userService.getUserInfo(request);
        Board board = boardService.findBoardById(boardId);

        // 이미 좋아요 누른 경우 확인
        BoardLike existingBoardLike = boardLikeRepository.findByUserAndBoard(user, board);

        if (existingBoardLike != null) {
            // 이미 좋아요 누른 경우: 좋아요 취소 (삭제)
            log.info("식별자(boardId): {}, 이미 좋아요 한 게시물 -> 좋아요 삭제", boardId);
            boardLikeRepository.delete(existingBoardLike);
            board.decrementLikeCount();
        } else {
            // 좋아요 누르지 않은 경우: 좋아요 추가
            log.info("식별자(boardId): {}, 게시글에 좋아요를 누릅니다.", boardId);
            Scrap scrap = Scrap.builder()
                    .user(user)
                    .board(board)
                    .build();

            BoardLike boardLike = BoardLike.builder()
                    .user(user)
                    .board(board)
                    .build();
            boardLikeRepository.save(boardLike);
            board.addLike(boardLike);
        }
    }
}

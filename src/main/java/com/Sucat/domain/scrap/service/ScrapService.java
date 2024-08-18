package com.Sucat.domain.scrap.service;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.scrap.model.Scrap;
import com.Sucat.domain.scrap.repository.ScrapRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapService {
    private final ScrapRepository scrapRepository;
    private final BoardService boardService;
    private final UserService userService;

    /* 스크랩 하기, 취소하기 메서드 */
    public void scrap(Long boardId, HttpServletRequest request) {
        User user = userService.getUserInfo(request);
        Board board = boardService.findBoardById(boardId);

        // 이미 스크랩한 경우 확인
        Scrap existingScrap = scrapRepository.findByUserAndBoard(user, board);

        if (existingScrap != null) {
            // 이미 스크랩한 경우: 스크랩 취소 (삭제)
            scrapRepository.delete(existingScrap);
            user.removeScrap(existingScrap);
            board.removeScrap(existingScrap);
        } else {
            // 스크랩하지 않은 경우: 스크랩 추가
            Scrap scrap = Scrap.builder()
                    .user(user)
                    .board(board)
                    .build();
            scrapRepository.save(scrap);
            user.addScrap(scrap);
            board.addScrap(scrap);
        }
    }
}

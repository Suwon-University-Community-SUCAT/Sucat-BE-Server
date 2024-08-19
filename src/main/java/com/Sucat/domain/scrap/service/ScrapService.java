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

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.BoardListResponse;

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
            log.info("식별자(boardId): {}, 이미 스크랩한 게시물 -> 스크랩 삭제", boardId);
            scrapRepository.delete(existingScrap);
        } else {
            // 스크랩하지 않은 경우: 스크랩 추가
            log.info("식별자(boardId): {}, 게시글을 스크랩합니다.", boardId);
            Scrap scrap = Scrap.builder()
                    .user(user)
                    .board(board)
                    .build();
            scrapRepository.save(scrap);
            user.addScrap(scrap);
            board.addScrap(scrap);
        }
    }

    /* 내가 스크랩한 게시물 불러오기 */
    public List<BoardListResponse> getMyScrap(HttpServletRequest request) {
        User user = userService.getUserInfo(request);

        return user.getScrapList().stream()
                .map(s -> BoardListResponse.of(s.getBoard()))
                .toList();
    }
}

package com.Sucat.domain.board.service;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.repository.BoardRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void createBoard(Board board, HttpServletRequest request) {
        User user = userService.getUserInfo(request);
        board.addUser(user);
        user.addBoard(board);
    }


    public BoardUpdateResponse getUpdateBoard(Long id, HttpServletRequest request) {
        Board board = findBoardById(id);
        validateUserAuthorization(request, board);

        return BoardUpdateResponse.of(board);
    }

    @Transactional
    public void updateBoard(Long id, BoardUpdateRequest requestDTO, HttpServletRequest request) {
        Board board = findBoardById(id);
        validateUserAuthorization(request, board);

        board.updateBoard(requestDTO.title(), requestDTO.content());
    }

    @Transactional
    public void deleteBoard(Long id, HttpServletRequest request) {
        Board board = findBoardById(id);

        validateUserAuthorization(request, board);

        boardRepository.deleteById(id);
    }

    //특정 카테고리의 게시글 목록 조회
    public BoardListResponseWithHotPost getAllBoards(BoardCategory category) {

        //TODO 페이징으로 수정
        List<BoardListResponse> boardListResponses = boardRepository.findByCategory(category).stream()
                .map(BoardListResponse::of
                ).toList();

        //TODO 쿼리 최적화 필요
        //핫포스트
        Board hotPost = boardRepository.findAll().stream()
                .max((a, b) -> Integer.compare(a.getLikeCount(), b.getLikeCount()))
                .orElseThrow(() -> new RuntimeException("No hotPost found"));

        HotPostResponse hotPostResponse = HotPostResponse.of(hotPost);

        return BoardListResponseWithHotPost.of(boardListResponses, hotPostResponse);
    }

    public BoardDetailResponse getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("Board not found"));
//        List<CommentPostResponse> comments = board.getComments().stream()
//                .map(comment -> new CommentPostResponse(
//                        comment.getUser().getName(),
//                        comment.getContent(),
//                        comment.getMinute().toString(),
//                        comment.getLikeCount(),
//                        comment.getCommentCount(),
//                        comment.getScrapCount()
//                        //comment.getUser().getImageUrl() // Assuming User has an imageUrl field
//                ))
//                .collect(Collectors.toList());

        return BoardDetailResponse.of(board);
    }

    /* Using Method */
    public Board findBoardById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new RuntimeException("No found"));
    }

    private void validateUserAuthorization(HttpServletRequest request, Board board) {
        User user = userService.getUserInfo(request);

        // 게시글 작성자만 수정 가능
        if (!board.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to delete this board");
        }
    }
}
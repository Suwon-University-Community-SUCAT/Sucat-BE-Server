package com.Sucat.domain.board.service;

import com.Sucat.domain.board.exception.BoardException;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.repository.BoardQueryRepository;
import com.Sucat.domain.board.repository.BoardRepository;
import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.image.service.ImageService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.Sucat.domain.board.dto.BoardDto.*;
import static com.Sucat.domain.comment.dto.CommentDto.CommentResponseWithBoard;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;

    /* 게시물 생성 조회 메서드 */
    @Transactional
    public void createBoard(Board board, HttpServletRequest request, List<MultipartFile> images) {
        User user = userService.getUserInfo(request);

        if (images == null) {
            images = Collections.emptyList();
        }

        if (!images.isEmpty()) {
            List<String> imageNames = imageService.storeFiles(images);

            List<Image> imageList = imageNames.stream()
                    .map(imageName -> Image.ofBoard(board, imageName))
                    .toList();

            board.addAllImage(imageList);
        }

        boardRepository.save(board);

        board.addUser(user);
        user.addBoard(board);
        log.info("게시글 생성, 연관관계 설정 완료");
    }


    /* 게시글 수정 메서드 - Get */
    public BoardUpdateResponse getUpdateBoard(Long id, HttpServletRequest request) {
        Board board = findBoardById(id);
        validateUserAuthorization(request, board);

        return BoardUpdateResponse.of(board);
    }

    /* 게시물 수정 메서드 - Post*/
    @Transactional
    public void updateBoard(Long id, BoardUpdateRequest requestDTO, HttpServletRequest request, List<MultipartFile> images) {
        Board board = findBoardById(id);
        validateUserAuthorization(request, board);

        if (images.isEmpty()) {
            board.updateBoard(requestDTO.title(), requestDTO.content());
            log.info("식별자: {}, 게시글 수정 완료-이미지 x", id);
        } else {
            List<String> imageNames = imageService.storeFiles(images);

            List<Image> imageList = imageNames.stream()
                    .map(image -> Image.ofBoard(board, image))
                    .toList();

            board.updateBoard(requestDTO.title(), requestDTO.content(), imageList);
            log.info("식별자: {}, 게시글 수정 완료-이미지 o", id);
        }
    }

    /* 게시물 삭제 메서드 */
    @Transactional
    public void deleteBoard(Long id, HttpServletRequest request) {
        Board board = findBoardById(id);
        validateUserAuthorization(request, board);

        List<String> imageNames = board.getImageList().stream()
                .map(i -> i.getImageName())
                .toList();

        imageService.deleteFiles(imageNames); // 이미지 폴더에서 이미지 삭제

        boardRepository.deleteById(id);
        log.info("식별자: {}, 게시물 삭제 완료", id);
    }

    /* 특정 카테고리 게시판 게시물 조회 메서드 */
    public BoardListResponseWithHotPost getAllBoards(BoardCategory category, Pageable pageable) {

        List<BoardListResponse> boardListResponses = boardRepository.findByCategory(category, pageable).stream()
                .map(BoardListResponse::of
                ).toList();

        // 3일 전 시간 계산
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);


        // 3일 이내에 작성된 게시물 중 가장 높은 likeCount를 가진 게시물을 찾는 쿼리
        Optional<Board> optionalHotPost = boardQueryRepository.findTopHotPost(category, threeDaysAgo);

        HotPostResponse hotPostResponse = optionalHotPost
                .map(HotPostResponse::of)
                .orElse(null);  // hotPost가 없으면 null로 설정

        return BoardListResponseWithHotPost.of(boardListResponses, hotPostResponse);
    }

    /* 게시물 단일 조회 메서드 */
    public BoardDetailResponse getBoard(Long id) {
        Board board = findBoardById(id);
        List<CommentResponseWithBoard> commentList = board.getCommentList().stream()
                .map(CommentResponseWithBoard::of)
                .toList();

        log.info("식별자: {}, 게시물 단일 조회 성공", id);
        return BoardDetailResponse.of(board, commentList);
    }

    /* 게시물 검색 메서드 */
    public List<BoardListResponse> getSearchBoard(BoardCategory category, String keyword, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findByCategoryAndTitleContaining(category, keyword, pageable);
        List<BoardListResponse> boardListResponses = boardPage.stream()
                .map(BoardListResponse::of)
                .toList();
        log.info("검색어: {}, 게시물 검색 성공", keyword);
        return boardListResponses;
    }

    /* 내가 쓴 게시물 조회 메서드 */
    public List<MyBoardResponse> myPost(HttpServletRequest request) {
        User user = userService.getUserInfo(request);
        return user.getBoardList().stream()
                .map(MyBoardResponse::of)
                .toList();
    }

    /* Using Method */
    public Board findBoardById(Long id) {
        return boardRepository.findById(id).orElseThrow(
                () -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
    }

    private void validateUserAuthorization(HttpServletRequest request, Board board) {
        User user = userService.getUserInfo(request);

        // 게시글 작성자만 수정 가능
        if (!board.getUser().equals(user)) {
            log.info("error: 게시글 작성자가 아닌 사용자의 접근");
            throw new BoardException(ErrorCode._UNAUTHORIZED_USER);
        }
    }
}
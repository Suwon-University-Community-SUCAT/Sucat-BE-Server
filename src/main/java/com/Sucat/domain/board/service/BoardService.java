package com.Sucat.domain.board.service;

import com.Sucat.domain.board.exception.BoardException;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;

    @Transactional
    public void createBoard(Board board, HttpServletRequest request, List<MultipartFile> images) {
        User user = userService.getUserInfo(request);

        List<String> imageNames = imageService.storeFiles(images);

        List<Image> imageList = imageNames.stream()
                .map(image -> Image.ofBoard(board, image))
                .toList();

        board.addAllImage(imageList);

        board.addUser(user);
        user.addBoard(board);
    }


    public BoardUpdateResponse getUpdateBoard(Long id, HttpServletRequest request) {
        Board board = findBoardById(id);
        validateUserAuthorization(request, board);

        return BoardUpdateResponse.of(board);
    }

    /* 게시글 수정 메서드 */
    @Transactional
    public void updateBoard(Long id, BoardUpdateRequest requestDTO, HttpServletRequest request, List<MultipartFile> images) {
        Board board = findBoardById(id);
        validateUserAuthorization(request, board);

        if (images.isEmpty()) {
            board.updateBoard(requestDTO.title(), requestDTO.content());
        } else {
            List<String> imageNames = imageService.storeFiles(images);

            List<Image> imageList = imageNames.stream()
                    .map(image -> Image.ofBoard(board, image))
                    .toList();

            board.updateBoard(requestDTO.title(), requestDTO.content(), imageList);
        }
    }

    @Transactional
    public void deleteBoard(Long id, HttpServletRequest request) {
        Board board = findBoardById(id);
        validateUserAuthorization(request, board);

        List<String> imageNames = board.getImageList().stream()
                .map(i -> i.getImageName())
                .toList();

        imageService.deleteFiles(imageNames); // 이미지 폴더에서 이미지 삭제

        boardRepository.deleteById(id);
    }

    //특정 카테고리의 게시글 목록 조회
    public BoardListResponseWithHotPost getAllBoards(BoardCategory category, Pageable pageable) {

        List<BoardListResponse> boardListResponses = boardRepository.findByCategory(category, pageable).stream()
                .map(BoardListResponse::of
                ).toList();

        //TODO 쿼리 최적화 필요
        //핫포스트
        Board hotPost = boardRepository.findAll().stream()
                .max((a, b) -> Integer.compare(a.getLikeCount(), b.getLikeCount()))
                .orElseThrow(() -> new BoardException(ErrorCode.BOARD_NOT_FOUND));

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
        return boardRepository.findById(id).orElseThrow(
                () -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
    }

    private void validateUserAuthorization(HttpServletRequest request, Board board) {
        User user = userService.getUserInfo(request);

        // 게시글 작성자만 수정 가능
        if (!board.getUser().equals(user)) {
            throw new BoardException(ErrorCode.UNAUTHORIZED_USER);
        }
    }
}
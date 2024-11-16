package com.Sucat.domain.board.service;

import com.Sucat.domain.board.exception.BoardException;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.repository.BoardQueryRepository;
import com.Sucat.domain.board.repository.BoardRepository;
import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.image.service.S3Uploader;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.Sucat.domain.board.dto.BoardDto.*;
import static com.Sucat.domain.comment.dto.CommentDto.CommentResponseWithBoard;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;
    private final S3Uploader s3Uploader;

    /* 게시물 생성 메서드 */
    @Transactional
    public void createBoard(Board board, User user, List<MultipartFile> images) throws IOException {
        if (images != null && !images.isEmpty()) {
            List<Map<String, String>> imageInfos = s3Uploader.uploadMultiple(images);

            List<Image> storedImages = imageInfos.stream()
                    .map(imageInfo -> Image.ofBoard(board, imageInfo.get("imageUrl"), imageInfo.get("imageName")))
                    .toList();

            board.addAllImage(storedImages);
        }

        board.addUser(user);
        boardRepository.save(board);
        user.addBoard(board);
        log.info("게시글 생성, 연관관계 설정 완료");
    }


    /* 게시글 수정 메서드 - Get */
    public BoardUpdateResponse getUpdateBoard(Long id, User user) {
        Board board = findBoardById(id);
        validateUserAuthorization(user, board);

        return BoardUpdateResponse.of(board);
    }
    /* 게시물 수정 메서드 */
    @Transactional
    public void updateBoard(Long id, BoardUpdateRequest requestDTO, User user, List<MultipartFile> images) {
        Board board = findBoardById(id);
        validateUserAuthorization(user, board);

        board.updateBoard(requestDTO.title(), requestDTO.content());
        log.info("식별자: {}, 게시글 수정 완료-이미지 x", id);


//        if (images != null && !images.isEmpty()) {
////            s3Uploader.updateFile(images)
//            List<String> imageNames = imageService.storeFiles(images);
//            List<Image> imageList = imageNames.stream()
//                    .map(imageName -> Image.ofBoard(board, imageName))
//                    .toList();
//            board.updateBoard(requestDTO.title(), requestDTO.content(), imageList);
//            log.info("식별자: {}, 게시글 수정 완료-이미지 o", id);
//        } else {
//            board.updateBoard(requestDTO.title(), requestDTO.content());
//            log.info("식별자: {}, 게시글 수정 완료-이미지 x", id);
//        }
    }

    /* 게시물 삭제 메서드 */
    @Transactional
    public void deleteBoard(Long id, User user) {
        Board board = findBoardById(id);
        validateUserAuthorization(user, board);

        List<String> fileNames = board.getImageList().stream()
                .map(i -> i.getImageName())
                .toList();

        // S3에서 이미지 파일 삭제
        for (String fileName : fileNames) {
            s3Uploader.deleteFile(fileName); // S3에서 파일 삭제
        }

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
    public BoardDetailResponse getBoard(Long id, User user) {
        Board board = findBoardById(id);
        Long currentUserId = user.getId();

        //현재 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
        boolean isLikedByUser = board.getLikeList().stream()
                .anyMatch(like -> like.getUser().getId().equals(currentUserId));
        boolean isScrapByUser = board.getScrapList().stream()
                .anyMatch(scrap -> scrap.getUser().getId().equals(currentUserId));

        // 댓글 리스트 정리
        List<CommentResponseWithBoard> commentList = board.getCommentList().stream()
                .map(comment -> CommentResponseWithBoard.of(comment, currentUserId))
                .toList();

        log.info("식별자: {}, 게시물 단일 조회 성공", id);
        return BoardDetailResponse.of(board, commentList, isLikedByUser, isScrapByUser);
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
    public List<MyBoardResponse> myPost(User user) {
        return user.getBoardList().stream()
                .map(MyBoardResponse::of)
                .toList();
    }

    /* Using Method */
    public Board findBoardById(Long id) {
        return boardRepository.findById(id).orElseThrow(
                () -> new BoardException(ErrorCode.BOARD_NOT_FOUND));
    }

    private void validateUserAuthorization(User user, Board board) {
        // 게시글 작성자만 수정 가능
        if (!board.getUser().equals(user)) {
            log.info("error: 게시글 ID {}의 작성자가 아닌 사용자 ID {}의 접근 시도", board.getId(), user.getId());
            throw new BoardException(ErrorCode._UNAUTHORIZED_USER);
        }
    }
}
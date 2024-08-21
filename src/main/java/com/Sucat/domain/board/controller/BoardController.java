package com.Sucat.domain.board.controller;

import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;

    /* 게시글 작성 */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createBoard(
            @RequestPart(name = "request") @Valid BoardPostRequest boardPostRequestDTO,
            @RequestPart(name = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request) {
        boardService.createBoard(boardPostRequestDTO.toEntity(), request, images);
        return ApiResponse.onSuccess(SuccessCode._CREATED);
    }

    /* (자유, 비밀, 중고장터) 카테고리의 게시글 목록 조회 */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getBoardsByCategory(
            @RequestParam(name = "category", defaultValue = "FREE") BoardCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
            ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        BoardListResponseWithHotPost allBoards = boardService.getAllBoards(category, pageable);

        return ApiResponse.onSuccess(SuccessCode._OK, allBoards);
    }

    /* 게시글 단일 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getBoard(@PathVariable Long id) {
        BoardDetailResponse board = boardService.getBoard(id);
        return ApiResponse.onSuccess(SuccessCode._OK, board);
    }

    /* 게시글 수정 */
    @GetMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<Object>> getUpdateBoard(@PathVariable("id") Long id,
                                                              HttpServletRequest request) {
        BoardUpdateResponse updateBoard = boardService.getUpdateBoard(id, request);

        return ApiResponse.onSuccess(SuccessCode._OK, updateBoard);
    }

    /* 게시글 수정 */
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<Object>> updateBoard(@PathVariable Long id,
                                                            @RequestPart(name = "request") @Valid BoardUpdateRequest boardUpdateRequest,
                                                            @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                                            HttpServletRequest request) {
        boardService.updateBoard(id, boardUpdateRequest, request, images);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 게시글 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBoard(@PathVariable Long id, HttpServletRequest request) {
        boardService.deleteBoard(id, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 게시글 검색 */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> searchBoard(
            @RequestParam(name = "category", defaultValue = "FREE") BoardCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(name = "keyword", defaultValue = "") @Nullable String keyword
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        List<BoardListResponse> searchBoard = boardService.getSearchBoard(category, keyword, pageable);

        return ApiResponse.onSuccess(SuccessCode._OK, searchBoard);
    }

    /* 내가 작성한 게시글 조회 */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Object>> myPost(
            HttpServletRequest request
    ) {
        List<MyBoardResponse> myBoardResponses = boardService.myPost(request);

        return ApiResponse.onSuccess(SuccessCode._OK, myBoardResponses);
    }
}

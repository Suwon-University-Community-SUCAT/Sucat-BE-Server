package com.Sucat.domain.board.controller;

import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
            @RequestPart(name = "request") BoardPostRequest boardPostRequestDTO,
            @RequestPart(name = "images", required = false) List<MultipartFile> images,
            HttpServletRequest request) {
        boardService.createBoard(boardPostRequestDTO.toEntity(), request, images);
        return ApiResponse.onSuccess(SuccessCode._CREATED);
    }

    /* (자유, 비밀, 중고장터) 카테고리의 게시글 목록 조회 */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getBoardsByCategory(
            @RequestParam(name = "category", defaultValue = "FREE") BoardCategory category,
            @PageableDefault(page = 0, size = 30) @Nullable final Pageable pageable) {
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
                                                            @RequestPart(name = "request") BoardUpdateRequest boardUpdateRequest,
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
}

package com.Sucat.domain.board.controller;

import com.Sucat.domain.board.dto.BoardPostRequestDTO;
import com.Sucat.domain.board.dto.BoardResponse;
import com.Sucat.domain.board.dto.BoardUpdateRequestDTO;
import com.Sucat.domain.board.dto.ResponseDTO;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;

    /* 게시글 작성 */
    @PostMapping
    public ResponseEntity<ApiResponse<Objects>> createBoard(@RequestBody BoardPostRequestDTO boardPostRequestDTO, HttpServletRequest request) {
        boardService.createBoard(boardPostRequestDTO, request);
        return ApiResponse.onSuccess(SuccessCode._CREATED);
    }

    /* (자유, 비밀, 중고장터) 카테고리의 게시글 목록 조회 */
    @GetMapping
    public ResponseEntity<ApiResponse<Objects>> getBoardsByCategory(@RequestParam BoardCategory category) {
        ResponseDTO allBoards = boardService.getAllBoards(category);

        return ApiResponse.onSuccess(SuccessCode._OK, allBoards);
    }

    /* 게시글 단일 조회 */
    @GetMapping("/{id}")
    public BoardResponse getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    /* 게시글 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Objects>> updateBoard(@PathVariable Long id,
                                                            @RequestBody BoardUpdateRequestDTO boardUpdateRequestDTO,
                                                            HttpServletRequest request) {
        boardService.updateBoard(id, boardUpdateRequestDTO, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 게시글 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBoard(@PathVariable Long id, HttpServletRequest request) {
        boardService.deleteBoard(id, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}

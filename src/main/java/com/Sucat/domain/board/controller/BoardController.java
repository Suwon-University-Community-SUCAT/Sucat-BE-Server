package com.Sucat.domain.board.controller;

import com.Sucat.domain.board.DTO.BoardPostRequestDTO;
import com.Sucat.domain.board.DTO.BoardResponse;
import com.Sucat.domain.board.DTO.ResponseDTO;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    /* 게시글 작성 */
    @PostMapping
    public ResponseEntity<ApiResponse<Objects>> createBoard(@RequestBody BoardPostRequestDTO boardPostRequestDTO,
                                                            HttpServletRequest request) {
        boardService.createBoard(boardPostRequestDTO, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 게시글 목록 조회 */
    @GetMapping
    public ResponseDTO getAllBoards() {
        return boardService.getAllBoards();
    }

    /* 게시글 단일 조회 */
    @GetMapping("/{id}")
    public BoardResponse getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    /* 게시글 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Objects>> updateBoard(@PathVariable Long id,
                                                            @RequestBody BoardPostRequestDTO boardPostRequestDTO,
                                                            HttpServletRequest request) {
        boardService.updateBoard(id, boardPostRequestDTO, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 게시글 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteBoard(@PathVariable Long id, HttpServletRequest request) {
        boardService.deleteBoard(id, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }
}

package com.Sucat.domain.board.model.controller;



import com.Sucat.domain.board.model.dto.BoardResponseDto;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.model.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 자유게시판 조회
    @GetMapping("/free")
    public ResponseEntity<List<BoardResponseDto>> getFreeBoards() {
        List<BoardResponseDto> boards = boardService.getBoardsByCategory(BoardCategory.FREE);
        return ResponseEntity.ok(boards);
    }

    // 사적게시판 조회
    @GetMapping("/private")
    public ResponseEntity<List<BoardResponseDto>> getPrivateBoards() {
        List<BoardResponseDto> boards = boardService.getBoardsByCategory(BoardCategory.PRIVATE);
        return ResponseEntity.ok(boards);
    }

    // 중고장터 게시판 조회
    @GetMapping("/market")
    public ResponseEntity<List<BoardResponseDto>> getMarketBoards() {
        List<BoardResponseDto> boards = boardService.getBoardsByCategory(BoardCategory.MARKET);
        return ResponseEntity.ok(boards);
    }

    // 게시판 생성
    @PostMapping
    public ResponseEntity<String> createBoard(@RequestBody Board board) {
        Board createdBoard = boardService.saveBoard(board);
        return ResponseEntity.ok("성공");
    }

    // 게시판 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable Long id) {
        Board board = boardService.getBoardById(id);
        if (board != null) {
            return ResponseEntity.ok(board);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 게시판 수정
    @PutMapping("edit/{id}")
    public ResponseEntity<String> updateBoard(@PathVariable Long id, @RequestBody Board updatedBoard) {
        Board board = boardService.updateBoard(id, updatedBoard);
        if (board != null) {
            return ResponseEntity.ok("성공");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 게시판 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}



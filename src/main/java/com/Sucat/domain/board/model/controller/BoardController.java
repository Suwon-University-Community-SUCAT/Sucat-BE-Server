package com.Sucat.domain.board.model.controller;



import com.Sucat.domain.board.model.dto.BoardRequestDto;
import com.Sucat.domain.board.model.dto.BoardResponseDto;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.model.service.BoardService;
import com.Sucat.domain.image.model.Image;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        Board createdBoard = boardService.saveBoard(board);
        return ResponseEntity.ok(createdBoard);
    }

    // 게시판 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Long id) {
        Board board = boardService.getBoardById(id);
        if (board != null) {
            // Board를 DTO로 변환
            BoardResponseDto responseDto = boardService.convertToDto(board);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // 게시판 수정
    @PutMapping("edit/{id}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long id, @RequestBody BoardRequestDto updatedBoardDto) {
        try {
            Board updatedBoard = boardService.updateBoard(id, updatedBoardDto);
            if (updatedBoard != null) {
                // 업데이트된 Board를 DTO로 변환
                BoardResponseDto responseDto = boardService.convertToDto(updatedBoard);
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // 게시판 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}



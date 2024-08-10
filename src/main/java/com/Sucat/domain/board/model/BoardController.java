package com.Sucat.domain.board.model;

import com.Sucat.domain.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public Board createBoard(@RequestParam String name, @RequestParam String title,
                             @RequestParam String content, @RequestParam BoardCategory category,
                             HttpServletRequest request) {
        return boardService.createBoard(name, title, content, category, request);
    }

    @GetMapping
    public ResponseDTO getAllBoards() {
        return boardService.getAllBoards();
    }

    @GetMapping("/{id}")
    public BoardResponse getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

}

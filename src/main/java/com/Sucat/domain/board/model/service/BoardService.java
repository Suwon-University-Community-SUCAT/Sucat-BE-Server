package com.Sucat.domain.board.model.service;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.board.model.dto.BoardResponseDto;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.model.repository.BoardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public List<BoardResponseDto> getBoardsByCategory(BoardCategory category) {
        return boardRepository.findAll().stream()
                .filter(board -> board.getCategory() == category)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    public Board getBoardById(Long id) {
        return boardRepository.findById(id).orElse(null);
    }

    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    public Board updateBoard(Long id, Board updatedBoard) {
        BoardResponseDto dto = new BoardResponseDto();
        return boardRepository.findById(id).map(existingBoard -> {
            dto.setName(updatedBoard.getName());
            dto.setTitle(updatedBoard.getTitle());
            dto.setContent(updatedBoard.getContent());
            dto.setFavoriteCount(updatedBoard.getFavoriteCount());
            //사진 수정 기능 추가해야함
            return boardRepository.save(existingBoard);
        }).orElse(null);
    }

    public Board likeBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        board.setFavoriteCount(board.getFavoriteCount() + 1);  // 좋아요 수 증가
        return boardRepository.save(board);    // 변경된 Board 엔티티 저장
    }

    public BoardResponseDto convertToDto(Board board) {
        BoardResponseDto dto = new BoardResponseDto();
        dto.setCreatedAtBoard(board.getUser().getCreatedAtBoard());  // Assuming you have a createdDate field in Board
        dto.setImageUrlList(board.getImages().stream()
                .map(image -> image.getImageUrl())
                .collect(Collectors.toList()));
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setName(board.getUser().getName());  // Assuming User entity has a getName() method
        dto.setFavoriteCount(board.getFavoriteCount());
        dto.setCommentCount(0);  // Assuming you need to calculate or fetch this
        dto.setScrapCount(0);    // Assuming you need to calculate or fetch this
        return dto;
    }
}


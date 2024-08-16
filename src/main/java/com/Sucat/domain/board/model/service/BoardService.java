package com.Sucat.domain.board.model.service;

import com.Sucat.domain.board.model.dto.BoardRequestDto;
import com.Sucat.domain.board.model.dto.BoardResponseDto;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.model.repository.BoardRepository;
import com.Sucat.domain.image.model.Image;
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

    public Board updateBoard(Long id, BoardRequestDto updatedBoardDto) {
        return boardRepository.findById(id).map(existingBoard -> {
            if (updatedBoardDto.getTitle() != null) {
                existingBoard.setTitle(updatedBoardDto.getTitle());
            }
            if (updatedBoardDto.getContent() != null) {
                existingBoard.setContent(updatedBoardDto.getContent());
            }
            if (updatedBoardDto.getImageUrlList() != null && !updatedBoardDto.getImageUrlList().isEmpty()) {
                existingBoard.setImages(
                        updatedBoardDto.getImageUrlList().stream()
                                .map(url -> Image.builder()
                                        .imageName(url)
                                        .board(existingBoard)
                                        .build())
                                .collect(Collectors.toList())
                );
            }
            return boardRepository.save(existingBoard);
        }).orElse(null);
    }

    public BoardResponseDto convertToDto(Board board) {
        BoardResponseDto dto = new BoardResponseDto();
        dto.setCreatedAtBoard(dto.getCreatedAtBoard());
        dto.setImageUrlList(board.getImages().stream()
                .map(Image::getImageName)
                .collect(Collectors.toList()));
        dto.setTitle(board.getTitle());
        dto.setContent(board.getContent());
        dto.setName(board.getUser().getName());
        dto.setFavoriteCount(board.getFavoriteCount());
        dto.setCommentCount(dto.getCommentCount());
        dto.setScrapCount(dto.getScrapCount());
        return dto;
    }

}


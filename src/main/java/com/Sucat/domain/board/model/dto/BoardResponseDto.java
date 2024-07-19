package com.Sucat.domain.board.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class BoardResponseDto {
    private LocalDateTime createdAtBoard;
    private List<String> imageUrlList;
    private String title;
    private String content;
    private String name;
    private int favoriteCount;
    private int commentCount;
    private int scrapCount;
}

package com.Sucat.domain.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardListResponseDTO {
    private String minute;
    // private List<String> imageUrlList;
    private String title;
    private String content;
    private String name;
    private int likeCount;
    private int commentCount;
    private int scrapCount;
}

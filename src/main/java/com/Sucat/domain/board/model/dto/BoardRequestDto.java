package com.Sucat.domain.board.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardRequestDto {
    private List<String> imageUrlList; // 업로드할 이미지 URL 리스트
    private String title;              // 게시글 제목
    private String content;            // 게시글 내용
}

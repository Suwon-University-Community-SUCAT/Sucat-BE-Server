package com.Sucat.domain.board.dto;

public class BoardPostDTO {
    private String minute;
    private String title;
    private String content;
    private String name;
    private int likeCount;
    private int commentCount;
    private int scrapCount;

    public BoardPostDTO(String minute, String title, String content, String name, int likeCount, int commentCount, int scrapCount) {
        this.minute = minute;
        this.title = title;
        this.content = content;
        this.name = name;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.scrapCount = scrapCount;

    }
}


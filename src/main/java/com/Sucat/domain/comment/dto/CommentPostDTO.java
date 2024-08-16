package com.Sucat.domain.comment.dto;

public class CommentPostDTO {
    private String name;
    private String commentContent;
    private String minute;
    private int likeCount;
    private int commentCount;
    private int scrapCount;
    
    public CommentPostDTO(String name, String commentContent, String minute, int likeCount, int commentCount, int scrapCount, String imageUrl) {
        this.name = name;
        this.commentContent = commentContent;
        this.minute = minute;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.scrapCount = scrapCount;
    }
}

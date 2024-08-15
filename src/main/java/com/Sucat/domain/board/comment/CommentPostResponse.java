package com.Sucat.domain.board.comment;

public class CommentPostResponse {
    private String name;
    private String commentContent;
    private String minute;
    private int likeCount;
    private int commentCount;
    private int scrapCount;

    public CommentPostResponse(String name, String commentContent, String minute, int likeCount, int commentCount, int scrapCount) {
        this.name = name;
        this.commentContent = commentContent;
        this.minute = minute;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.scrapCount = scrapCount;
    }
}

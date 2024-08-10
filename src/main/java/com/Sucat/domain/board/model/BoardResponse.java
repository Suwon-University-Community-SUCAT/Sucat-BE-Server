package com.Sucat.domain.board.model;

import java.util.List;

public class BoardResponse {
    private String minute;
   // private List<String> imageUrlList;
    private String title;
    private String content;
    private String name;
    private int likeCount;
    private int commentCount;
    private int scrapCount;
    private List<CommentPostResponse> comments;

    public BoardResponse(String minute, String title, String content, String name, int likeCount, int commentCount, int scrapCount) {
        this.minute = minute;
        //this.imageUrlList = imageUrlList;
        this.title = title;
        this.content = content;
        this.name = name;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.scrapCount = scrapCount;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getScrapCount() {
        return scrapCount;
    }

    public void setScrapCount(int scrapCount) {
        this.scrapCount = scrapCount;
    }

    public List<CommentPostResponse> getComments() {
        return comments;
    }

    public void setComments(List<CommentPostResponse> comments) {
        this.comments = comments;
    }
}

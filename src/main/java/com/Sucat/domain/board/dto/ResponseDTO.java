package com.Sucat.domain.board.dto;

import java.util.List;

public class ResponseDTO {
    private List<BoardResponse> post;
    private HotPostResponse hotPost;

    public ResponseDTO(List<BoardResponse> post, HotPostResponse hotPost) {
        this.post = post;
        this.hotPost = hotPost;
    }

    public List<BoardResponse> getPost() {
        return post;
    }

    public void setPost(List<BoardResponse> post) {
        this.post = post;
    }

    public HotPostResponse getHotPost() {
        return hotPost;
    }

    public void setHotPost(HotPostResponse hotPost) {
        this.hotPost = hotPost;
    }

    public static class HotPostResponse {
        private String title;
        private int likeCount;

        public HotPostResponse(String title, int likeCount) {
            this.title = title;
            this.likeCount = likeCount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }
    }
}

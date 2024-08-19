package com.Sucat.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentPostDTO {
    private String commentContent;

    @JsonCreator
    public CommentPostDTO(@JsonProperty("commentContent") String commentContent) {
       this.commentContent = commentContent;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}

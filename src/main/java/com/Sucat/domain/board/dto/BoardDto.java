package com.Sucat.domain.board.dto;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.image.model.Image;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

import static com.Sucat.domain.comment.dto.CommentDto.CommentResponseWithBoard;

public class BoardDto {
    /**
     * Request
     */
    public record BoardPostRequest(
            @NotNull
            String title,
            @NotNull
            String content,
            @NotNull
            BoardCategory category
    ) {
        public Board toEntity() {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .category(category)
                    .build();
        }
    }

    public record BoardUpdateRequest(
            @NotNull
            String title,
            @NotNull
            String content
    ) {

    }

    /**
     * Response
     */
    @Builder
    public record BoardListResponse(
            Long boardId,
            String title,
            String content,
            String userNickname,
            List<String> imageNames,
            int likeCount,
            int commentCount,
            int scrapCount,
            LocalDateTime createdAt
    ) {
        public static BoardListResponse of(Board board) {
            List<String> imageNames = board.getImageList().stream()
                    .map(Image::getImageName)
                    .toList();
            return BoardListResponse.builder()
                    .boardId(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .userNickname(board.getUser().getNickname())
                    .imageNames(imageNames)
                    .likeCount(board.getLikeCount())
                    .commentCount(board.getCommentCount())
                    .scrapCount(board.getScrapCount())
                    .createdAt(board.getCreatedAt())
                    .build();
        }
    }

    @Builder
    public record HotPostResponse(
            Long boardId,
            @NotNull
            String title,
            int likeCount
    ) {
        public static HotPostResponse of(Board board) {
            return HotPostResponse.builder()
                    .boardId(board.getId())
                    .title(board.getTitle())
                    .likeCount(board.getLikeCount())
                    .build();
        }
    }

    public record BoardListResponseWithHotPost(
            List<BoardListResponse> boardListResponses,
            HotPostResponse hotPostResponse
            ) {
        public static BoardListResponseWithHotPost of(List<BoardListResponse> boardListResponses, HotPostResponse hotPostResponse) {
            return new BoardListResponseWithHotPost(boardListResponses, hotPostResponse);
        }
    }

    // TODO 댓글 기능 개발 후 추가
    @Builder
    public record BoardDetailResponse(
            Long boardId,
            String title,
            String content,
            String userNickname,
            List<String> imageNames,
            int likeCount,
            int scrapCount,
            int commentCount,
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
            LocalDateTime createdAt,
             List<CommentResponseWithBoard> commentList
    ) {
        public static BoardDetailResponse of(Board board, List<CommentResponseWithBoard> commentList) {
            List<String> imageNames = board.getImageList().stream()
                    .map(Image::getImageName)
                    .toList();
            return BoardDetailResponse.builder()
                    .boardId(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .userNickname(board.getUser().getNickname())
                    .imageNames(imageNames)
                    .likeCount(board.getLikeCount())
                    .scrapCount(board.getScrapCount())
                    .commentCount(board.getCommentCount())
                    .createdAt(board.getCreatedAt())
                    .commentList(commentList)
                    .build();
        }
    }

    @Builder
    public record BoardUpdateResponse(
            String title,
            String content,
            List<String> imageNames
    ) {
        public static BoardUpdateResponse of(Board board) {
            List<String> imageNames = board.getImageList().stream()
                    .map(Image::getImageName)
                    .toList();
            return BoardUpdateResponse.builder()
                    .title(board.getTitle())
                    .content(board.getContent())
                    .imageNames(imageNames)
                    .build();
        }
    }

    @Builder
    public record MyBoardResponse(
            Long boardId,
            String title,
            String content,
            String userNickname,
            List<String> imageNames,
            int likeCount,
            int commentCount,
            int scrapCount,
            LocalDateTime createdAt
    ) {
        public static MyBoardResponse of(Board board) {
            List<String> imageNames = board.getImageList().stream()
                    .map(Image::getImageName)
                    .toList();
            return MyBoardResponse.builder()
                    .boardId(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .userNickname(board.getUser().getNickname())
                    .imageNames(imageNames)
                    .likeCount(board.getLikeCount())
                    .commentCount(board.getCommentCount())
                    .scrapCount(board.getScrapCount())
                    .createdAt(board.getCreatedAt())
                    .build();
        }
    }
}

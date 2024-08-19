package com.Sucat.domain.comment.service;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.repository.BoardRepository;
import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.comment.dto.CommentPostDTO;
import com.Sucat.domain.comment.dto.CommentPostResponse;
import com.Sucat.domain.comment.repository.CommentRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public CommentPostResponse createComment(Long boardId, Long userId, CommentPostDTO commentPostDTO) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board id: " + boardId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + userId));

        Comment comment = new Comment(board, user, commentPostDTO.getCommentContent());
        Comment savedComment = commentRepository.save(comment);

        return new CommentPostResponse(
                user.getName(),
                savedComment.getContent(),
                savedComment.getMinute().toString(),
                savedComment.getLikeCount(),
                savedComment.getCommentCount()
                //savedComment.getScrapCount()
        );
    }

    public CommentPostResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comment id: " + commentId));
        User user = comment.getUser();

        return new CommentPostResponse(
                user.getName(),
                comment.getContent(),
                comment.getMinute().toString(),
                comment.getLikeCount(),
                comment.getCommentCount()
                //comment.getScrapCount()
        );
    }
}

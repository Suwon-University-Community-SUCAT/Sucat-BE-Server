package com.Sucat.domain.like.repository;

import com.Sucat.domain.comment.domain.Comment;
import com.Sucat.domain.like.model.CommentLike;
import com.Sucat.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    CommentLike findByUserAndComment(User user, Comment comment);
}

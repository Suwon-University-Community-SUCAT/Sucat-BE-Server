package com.Sucat.domain.notify.model;

public enum NotifyType {
    HOT_POST,            // 핫게시물
    POST_COMMENT,        // 게시물 댓글
    COMMENT_REPLY,       // 게시글 댓글의 대댓글
    FRIEND_REQUEST,      // 친구 추가 요청
    FRIEND_ACCEPTED,     // 친구 추가 수락
    CHAT_MESSAGE,        // 채팅
    NOTICE               // 공지사항
}

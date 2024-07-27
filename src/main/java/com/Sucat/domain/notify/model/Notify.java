package com.Sucat.domain.notify.model;

import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.dao.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notify extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "notify_id")
    private Long id;

    private String notifyId;

    private String content;

    private String url;

    @Column(nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotifyType notifyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Builder
    public Notify(String notifyId, String content, String url, Boolean isRead, NotifyType notifyType, User user) {
        this.notifyId = notifyId;
        this.content = content;
        this.url = url;
        this.isRead = isRead;
        this.notifyType = notifyType;
        this.user = user;
    }
}

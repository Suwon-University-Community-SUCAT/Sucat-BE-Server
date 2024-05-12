package com.Sucat.domain.notify.model;

import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notify {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "notify_id")
    private Long id;

    private String url;

    private Boolean isRead;

    private NotifyType notifyType;

    @NotNull
    private Long receiverId; // 연관관계 설정으로 각 알림이 어떤 유저에 대한 알림인지 알 수 있다. 이 속성이 필요할까?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

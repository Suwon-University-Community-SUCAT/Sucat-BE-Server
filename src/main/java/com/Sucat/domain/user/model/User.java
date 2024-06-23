package com.Sucat.domain.user.model;

import com.Sucat.domain.model.BaseEntity;
import com.Sucat.domain.friendship.model.FriendShip;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users")// User는 예약어이기에 사용 불가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Email
    private String email;

    private String name;

    @NotNull
    private String password;

    private String department;

    private String nickname;

    private UserRole role = UserRole.USER_ROLE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FriendShip> mates = new ArrayList<>();

    @Builder
    public User(String name, String email, String password, String department, String nickname) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.department = department;
        this.nickname = nickname;
    }

}

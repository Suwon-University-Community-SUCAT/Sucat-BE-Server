package com.Sucat.domain.user.model;

import com.Sucat.domain.model.BaseEntity;
import com.Sucat.domain.friendship.model.FriendShip;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
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

    @NotNull
    private String password;


    @NotNull
    private String department;

    private String nickName;

    @NotNull
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FriendShip> mates = new ArrayList<>();


//    private UserStatus userStatus;

}

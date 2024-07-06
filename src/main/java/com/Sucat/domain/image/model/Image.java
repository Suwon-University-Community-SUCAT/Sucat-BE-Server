package com.Sucat.domain.image.model;

import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Long id;

    @NotNull
//    @URL
    private String imageUrl;

    @ManyToOne(fetch = LAZY)
    @NotNull
    @JoinColumn(name = "user_id")
    private User user;

    public static Image of(User user, String imageUrl) {

        return Image.builder()
                .user(user) //연관관계 설정
                .imageUrl(imageUrl)
                .build();

    }
}

package com.Sucat.domain.image.model;

import com.Sucat.domain.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
//    @URL /* 올바른 url 형식이 아니라면 검증 부분에서 에러를 발생시킴 */
    private String imageUrl;

    @OneToOne
    private User user;

    public static Image of(User user, String imageUrl) {

        return Image.builder()
                .user(user) //연관관계 설정
                .imageUrl(imageUrl)
                .build();

    }
}

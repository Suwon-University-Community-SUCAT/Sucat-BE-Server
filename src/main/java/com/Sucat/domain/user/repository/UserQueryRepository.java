package com.Sucat.domain.user.repository;

import com.Sucat.domain.user.model.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final EntityManager em;

    public User findUserProfileByEmail(String email) {
        return em.createQuery(
                "select u from User u " +
                        "LEFT join fetch u.userImage ui " +
                        "where u.email = :email", User.class)
                .setParameter("email", email)
                .getSingleResult();
    }
}

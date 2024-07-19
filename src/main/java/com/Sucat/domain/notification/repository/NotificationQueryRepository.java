package com.Sucat.domain.notification.repository;

import com.Sucat.domain.notification.model.Notification;
import com.Sucat.domain.user.model.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {
    private final EntityManager em;

    public Notification findNotificationById(Long id) {
        return em.createQuery(
                        "select n from Notification n " +
                                "LEFT join fetch n.images i " +
                                "where n.id = :id", Notification.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}

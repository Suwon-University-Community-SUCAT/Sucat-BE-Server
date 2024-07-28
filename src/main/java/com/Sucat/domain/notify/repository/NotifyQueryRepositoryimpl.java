package com.Sucat.domain.notify.repository;

import com.Sucat.domain.notify.model.Notify;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotifyQueryRepositoryimpl implements NotifyQueryRepository {
    private final EntityManager em;

    @Override
    public List<Notify> findByUserId(Long userId, LocalDateTime date) {
        return em.createQuery(
                        "select n " +
                                "from Notify n " +
                                "where n.user.id = :userId and n.createdAt > :date " +
                                "order by n.createdAt desc", Notify.class)
                .setParameter("userId", userId)
                .setParameter("date", date)
                .getResultList();
    }
}

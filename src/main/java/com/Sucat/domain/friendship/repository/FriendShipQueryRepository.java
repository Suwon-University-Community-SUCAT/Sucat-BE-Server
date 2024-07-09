package com.Sucat.domain.friendship.repository;

import com.Sucat.domain.friendship.dto.WaitingFriendDto;
import com.Sucat.domain.friendship.model.FriendshipStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendShipQueryRepository {
    private final EntityManager em;

    public List<WaitingFriendDto> findPendingFriendShipsByEmail(String userEmail) {
        return em.createQuery(
                        "select new com.Sucat.domain.friendship.dto.WaitingFriendDto(f.id, f.userEmail, u.nickname) " +
                                "from FriendShip f join User u on f.friendEmail = u.email " +
                                "where f.friendEmail = :userEmail and f.isFrom = true and f.status = :status", WaitingFriendDto.class)
                .setParameter("userEmail", userEmail)
                .setParameter("status", FriendshipStatus.WAITING)
                .getResultList();
    }
}

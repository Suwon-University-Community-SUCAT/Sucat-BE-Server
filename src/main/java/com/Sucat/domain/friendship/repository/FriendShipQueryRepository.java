package com.Sucat.domain.friendship.repository;

import com.Sucat.domain.friendship.dto.AcceptFriendDto;
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
                        "select new com.Sucat.domain.friendship.dto.WaitingFriendDto(f.id, f.friendEmail, u.nickname) " +
                                "from FriendShip f join User u on f.friendEmail = u.email " +
                                "where f.userEmail = :userEmail and f.isFrom = false and f.status = :status", WaitingFriendDto.class)
                .setParameter("userEmail", userEmail)
                .setParameter("status", FriendshipStatus.WAITING)
                .getResultList();
    }

    public List<AcceptFriendDto> findAcceptFriendShipsByEmail(String userEmail) {
        return em.createQuery(
                        "select new com.Sucat.domain.friendship.dto.AcceptFriendDto(f.id, u.email,u.nickname,u.department,u.intro) " +
                                "from FriendShip f join User u on f.friendEmail = u.email " +
                                "where f.userEmail = :userEmail and f.status = :status", AcceptFriendDto.class)
                .setParameter("userEmail", userEmail)
                .setParameter("status", FriendshipStatus.ACCEPT)
                .getResultList();
    }
}

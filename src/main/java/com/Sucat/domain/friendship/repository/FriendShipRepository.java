package com.Sucat.domain.friendship.repository;

import com.Sucat.domain.friendship.model.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {

    boolean existsByUserEmailAndFriendEmail(String userEmail, String friendEmail);
    Optional<FriendShip> findByUserEmailAndFriendEmail(String userEmail, String friendEmail);
    FriendShip findByUserEmail(String userEmail);
    FriendShip findByFriendEmail(String friendEmail);
}

package com.Sucat.domain.friendship.repository;

import com.Sucat.domain.friendship.dto.FriendListResponse;
import com.Sucat.domain.friendship.model.FriendShip;
import com.Sucat.domain.friendship.model.FriendshipStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {

    boolean existsByUserEmailAndFriendEmail(String userEmail, String friendEmail);
    Optional<FriendShip> findByUserEmailAndFriendEmail(String userEmail, String friendEmail);
    FriendShip findByUserEmail(String userEmail);
    FriendShip findByFriendEmail(String friendEmail);

    @Query("select new com.Sucat.domain.friendship.dto.FriendListResponse(f.id, u.email, u.nickname, u.department, u.intro, ui.imageUrl) " +
            "from FriendShip f " +
            "join User u on f.friendEmail = u.email " +
            "LEFT join u.userImage ui on u.userImage.id = ui.id " +
            "where f.userEmail = :userEmail and f.status = :status")
    Page<FriendListResponse> findAcceptFriendShipsByEmail(@Param("userEmail") String userEmail,
                                                          @Param("status") FriendshipStatus status,
                                                          Pageable pageable);
}

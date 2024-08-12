package com.Sucat.domain.friendship.repository;

import com.Sucat.domain.friendship.dto.FriendListResponse;
import com.Sucat.domain.friendship.dto.WaitingFriendDto;
import com.Sucat.domain.friendship.model.FriendshipStatus;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.util.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendShipQueryRepository {
    private final EntityManager em;
    private final JwtUtil jwtUtil;

    public List<WaitingFriendDto> findPendingFriendShipsByEmail(String userEmail) {
        return em.createQuery(
                        "select new com.Sucat.domain.friendship.dto.WaitingFriendDto(f.id, f.friendEmail, u.nickname, ui.imageName) " +
                                "from FriendShip f " +
                                "join User u on f.friendEmail = u.email " +
                                "LEFT join u.userImage ui on u.userImage.id = ui.id " +
                                "where f.userEmail = :userEmail and f.isFrom = false and f.status = :status", WaitingFriendDto.class)
                .setParameter("userEmail", userEmail)
                .setParameter("status", FriendshipStatus.WAITING)
                .getResultList();
    }

    public List<FriendListResponse> findAcceptFriendShipsByEmail(String userEmail) {
        return em.createQuery(
                        "select new com.Sucat.domain.friendship.dto.FriendListResponse(f.id, u.email, u.nickname, u.department, u.intro, ui.imageName) " +
                                "from FriendShip f " +
                                "join User u on f.friendEmail = u.email " +
                                "LEFT join u.userImage ui on u.userImage.id = ui.id " +
                                "where f.userEmail = :userEmail and f.status = :status", FriendListResponse.class)
                .setParameter("userEmail", userEmail)
                .setParameter("status", FriendshipStatus.ACCEPT)
                .getResultList();
    }

    public List<FriendListResponse> getSearchFriend(final String keyword, final Pageable pageable, final String sortkey, HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);

        String queryStr = "select new com.Sucat.domain.friendship.dto.FriendListResponse(f.id, u.email, u.nickname, u.department, u.intro, ui.imageName) " +
                "from FriendShip f " +
                "join User u on f.friendEmail = u.email " +
                "LEFT join u.userImage ui on u.userImage.id = ui.id " +
                "where f.userEmail = :userEmail and f.status = :status and " +
                "(u.nickname like :keyword)";

        String orderByClause;
        switch (sortkey) {
            case "name":
                orderByClause = " order by u.nickname asc";
                break;
            case "createAt":
                orderByClause = " order by f.createAt desc";
                break;
            default:
                orderByClause = " order by f.createdAt asc";
                break;
        }

        queryStr += orderByClause;

        return em.createQuery(queryStr, FriendListResponse.class)
                .setParameter("userEmail", user.getEmail())
                .setParameter("status", FriendshipStatus.ACCEPT)
                .setParameter("keyword", "%" + keyword + "%")
//                .setFirstResult((int) pageable.getOffset())
//                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}

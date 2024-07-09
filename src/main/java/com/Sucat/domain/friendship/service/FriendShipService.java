package com.Sucat.domain.friendship.service;

import com.Sucat.domain.friendship.model.FriendShip;
import com.Sucat.domain.friendship.model.FriendshipStatus;
import com.Sucat.domain.friendship.repository.FriendShipRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendShipService {
    private final FriendShipRepository friendShipRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public void createFriendShip(HttpServletRequest request, String toEmail) {
        User toUser = userService.findByEmail(toEmail);
        User fromUser = jwtUtil.getUserFromRequest(request);

        FriendShip friendShipFrom = FriendShip.builder()
                .user(fromUser)
                .friendEmail(fromUser.getEmail())
                .friendEmail(toEmail)
                .status(FriendshipStatus.WAITING)
                .isFrom(true) // 받은 요청이다.
                .build();

        FriendShip friendShipTo = FriendShip.builder()
                .user(toUser)
                .friendEmail(toEmail)
                .friendEmail(fromUser.getEmail())
                .status(FriendshipStatus.WAITING)
                .isFrom(false) // 보낸 요청이다.
                .build();

        // 각각 친구 리스트에 추가
        fromUser.addFriendShip(friendShipTo);
        toUser.addFriendShip(friendShipFrom);

        friendShipRepository.save(friendShipTo);
        friendShipRepository.save(friendShipFrom);

        // 매칭되는 친구 요청의 아이디를 서로 저장
        friendShipTo.setCounterpartId(friendShipFrom.getId());
        friendShipFrom.setCounterpartId(friendShipTo.getId());
    }
}

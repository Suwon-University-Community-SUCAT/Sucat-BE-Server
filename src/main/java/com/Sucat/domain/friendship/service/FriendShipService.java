package com.Sucat.domain.friendship.service;

import com.Sucat.domain.friendship.dto.WaitingFriendDto;
import com.Sucat.domain.friendship.exception.FriendShipException;
import com.Sucat.domain.friendship.model.FriendShip;
import com.Sucat.domain.friendship.model.FriendshipStatus;
import com.Sucat.domain.friendship.repository.FriendShipQueryRepository;
import com.Sucat.domain.friendship.repository.FriendShipRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendShipService {
    private final FriendShipRepository friendShipRepository;
    private final FriendShipQueryRepository friendShipQueryRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public FriendShip getFriendShip(Long id) {
        return friendShipRepository.findById(id)
                .orElseThrow(() -> new FriendShipException(ErrorCode.Friendship_NOT_FOUND));
    }

    @Transactional
    public void createFriendShip(HttpServletRequest request, String toEmail) {
        User fromUser = jwtUtil.getUserFromRequest(request);
        if (toEmail == fromUser.getEmail()) {
            throw new FriendShipException(ErrorCode.SELF_FRIENDSHIP_REQUEST);
        }
        User toUser = userService.findByEmail(toEmail);

        if (!checkReverseFriendship(toEmail, fromUser.getEmail())) {
            checkFriendshipAlready(toEmail, fromUser.getEmail());
            FriendShip friendShipFrom = FriendShip.builder()
                    .user(fromUser)
                    .userEmail(fromUser.getEmail())
                    .friendEmail(toEmail)
                    .status(FriendshipStatus.WAITING)
                    .isFrom(true) // 받은 요청이다.
                    .build();

            FriendShip friendShipTo = FriendShip.builder()
                    .user(toUser)
                    .userEmail(toEmail)
                    .friendEmail(fromUser.getEmail())
                    .status(FriendshipStatus.ACCEPT)
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

    public List<WaitingFriendDto> getWaitingFriendList(HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        return friendShipQueryRepository.findPendingFriendShipsByEmail(user.getEmail());
    }

    public void approveFriendshipRequest(Long friendshipId) {
        FriendShip friendShip = getFriendShip(friendshipId);
        FriendShip counterFriendship = getFriendShip(friendShip.getCounterpartId());

        // 상태를 ACCEPT로 변경
        friendShip.acceptFriendshipRequest();
        counterFriendship.acceptFriendshipRequest();
    }

    private void checkFriendshipAlready(String toEmail, String fromEmail) {
        // 이미 존재하는 친구 요청인지 확인
        boolean exist = friendShipRepository.existsByUserEmailAndFriendEmail(fromEmail, toEmail);
        if (exist) {
            throw new FriendShipException(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }
    }

    private boolean checkReverseFriendship(String toEmail, String fromEmail) {
        Optional<FriendShip>  reverseFriendshipOpt = friendShipRepository.findByUserEmailAndFriendEmail(toEmail, fromEmail);

        if (reverseFriendshipOpt.isPresent()){
            FriendShip reverseFriendship = reverseFriendshipOpt.get();
            if (reverseFriendship.getStatus() == FriendshipStatus.WAITING) {
                // 받은 친구 요청이 존재한다면, 자동으로 친구 수락
                acceptFriendship(reverseFriendship);
                return true;
            } else {

                throw new FriendShipException(ErrorCode.REVERSE_FRIENDSHIP_ALREADY_EXISTS);
            }
        }
        return false;
    }

    private void acceptFriendship(FriendShip reverseFriendship) {
        reverseFriendship.acceptFriendshipRequest();
        friendShipRepository.save(reverseFriendship);

        FriendShip counterpart = getFriendShip(reverseFriendship.getCounterpartId());
        counterpart.acceptFriendshipRequest();
        friendShipRepository.save(counterpart);
    }
}

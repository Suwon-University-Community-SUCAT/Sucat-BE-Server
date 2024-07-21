package com.Sucat.domain.friendship.service;

import com.Sucat.domain.friendship.dto.AcceptFriendDto;
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

import static com.Sucat.global.common.code.ErrorCode.INVALID_FRIENDSHIP_REQUEST_USER;

@Service
@RequiredArgsConstructor
public class FriendShipService {
    private final FriendShipRepository friendShipRepository;
    private final FriendShipQueryRepository friendShipQueryRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public FriendShip getFriendShipById(Long id) {
        return friendShipRepository.findById(id)
                .orElseThrow(() -> new FriendShipException(ErrorCode.Friendship_NOT_FOUND));
    }

    @Transactional
    public void createFriendShip(HttpServletRequest request, String toEmail) {
        User fromUser = jwtUtil.getUserFromRequest(request);
        String fromEmail = fromUser.getEmail();

        if (toEmail.equals(fromEmail)) {
            throw new FriendShipException(ErrorCode.SELF_FRIENDSHIP_REQUEST);
        }
        User toUser = userService.findByEmail(toEmail);

        if (!checkReverseFriendship(fromEmail, toEmail)) {

            FriendShip friendShipFrom = FriendShip.builder()
                    .user(fromUser)
                    .userEmail(fromEmail)
                    .friendEmail(toEmail)
                    .status(FriendshipStatus.WAITING)
                    .isFrom(true) // 보낸 요청이다.
                    .build();

            FriendShip friendShipTo = FriendShip.builder()
                    .user(toUser)
                    .userEmail(toEmail)
                    .friendEmail(fromEmail)
                    .status(FriendshipStatus.WAITING)
                    .isFrom(false) // 받은 요청이다.
                    .build();

            friendShipRepository.save(friendShipFrom);
            friendShipRepository.save(friendShipTo);

            // 각각 친구 리스트에 추가
            fromUser.addFriendShip(friendShipFrom);
            toUser.addFriendShip(friendShipTo);

            // 매칭되는 친구 요청의 아이디를 서로 저장
            friendShipFrom.setCounterpartId(friendShipTo.getId());
            friendShipTo.setCounterpartId(friendShipFrom.getId());
        }
    }

    public List<WaitingFriendDto> getWaitingFriendList(HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        return friendShipQueryRepository.findPendingFriendShipsByEmail(user.getEmail());
    }

    /* 친구 목록 조회 */
    public List<AcceptFriendDto> getAcceptFriendList(HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        return friendShipQueryRepository.findAcceptFriendShipsByEmail(user.getEmail());
    }

    @Transactional
    public void approveFriendshipRequest(Long friendshipId, HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        FriendShip friendShip = getFriendShipById(friendshipId);

        if (!user.getEmail().equals(friendShip.getUserEmail()) || friendShip.isFrom()) {
            throw new FriendShipException(ErrorCode.FRIENDSHIP_ACCEPT_NOT_ALLOWED);
        }

        FriendShip countFriendShip = getFriendShipById(friendShip.getCounterpartId());
        // 상태를 ACCEPT로 변경
        friendShip.acceptFriendshipRequest();
        countFriendShip.acceptFriendshipRequest();
    }

    @Transactional
    public void refuseFriendshipRequest(Long friendshipId, HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        FriendShip friendShip = getFriendShipById(friendshipId);

        if (user.getEmail().equals(friendShip.getUserEmail())) {
            throw new FriendShipException(ErrorCode.FRIENDSHIP_DECLINE_NOT_ALLOWED);
        }

        friendShipRepository.deleteById(friendShip.getId());
        friendShipRepository.deleteById(friendShip.getCounterpartId());
    }

    /* 친구 삭제 */
    @Transactional
    public void unfriend(HttpServletRequest request, Long fromFriendshipId) {
        String userEmail = userService.getUserInfo(request).getEmail();
        FriendShip fromFriendShip = getFriendShipById(fromFriendshipId);

        if (!fromFriendShip.getUserEmail().equals(userEmail)) {
            throw new FriendShipException(INVALID_FRIENDSHIP_REQUEST_USER);
        }

        FriendShip toFriendShip = friendShipRepository.findByFriendEmail(userEmail);
        Long toFriendShipId = toFriendShip.getId();

        if ((fromFriendShip.getStatus().equals(FriendshipStatus.WAITING)) || (toFriendShip.getStatus().equals(FriendshipStatus.WAITING))) {
            throw new FriendShipException(INVALID_FRIENDSHIP_REQUEST_USER);
        }

        friendShipRepository.deleteById(fromFriendshipId);
        friendShipRepository.deleteById(toFriendShipId);
    }

    /* Using Method */
    private boolean checkReverseFriendship(String fromEmail, String toEmail) {
        Optional<FriendShip>  reverseFriendshipOpt = friendShipRepository.findByUserEmailAndFriendEmail(fromEmail, toEmail);

        if (reverseFriendshipOpt.isPresent()){
            FriendShip reverseFriendship = reverseFriendshipOpt.get();

            checkFriendshipAlready(reverseFriendship); // 이미 친구인지, 기존에 보낸적이 있는 요청인지 검증

            if (reverseFriendship.getStatus() == FriendshipStatus.WAITING) {
                if (!reverseFriendship.isFrom()) {
                    // 받은 친구 요청이 존재한다면, 자동으로 친구 수락
                    acceptFriendship(reverseFriendship);
                    return true;
                }
            }
        }
        return false;
    }

    private void checkFriendshipAlready(FriendShip friendShip) {

        if (friendShip.getStatus().equals(FriendshipStatus.ACCEPT)) { // 이미 친구인지 검증
            throw new FriendShipException(ErrorCode.REVERSE_FRIENDSHIP_ALREADY_EXISTS);
        } else if (friendShip.isFrom()) { // 이미 보낸적 있는 요청인지 검증
            throw new FriendShipException(ErrorCode.FRIENDSHIP_ALREADY_EXISTS);
        }

    }

    private void acceptFriendship(FriendShip reverseFriendship) {
        reverseFriendship.acceptFriendshipRequest();
        friendShipRepository.save(reverseFriendship);

        FriendShip counterpart = getFriendShipById(reverseFriendship.getCounterpartId());
        counterpart.acceptFriendshipRequest();
        friendShipRepository.save(counterpart);
    }
}

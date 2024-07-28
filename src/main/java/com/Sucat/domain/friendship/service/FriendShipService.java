package com.Sucat.domain.friendship.service;

import com.Sucat.domain.friendship.dto.AcceptFriendDto;
import com.Sucat.domain.friendship.dto.WaitingFriendDto;
import com.Sucat.domain.friendship.exception.FriendShipException;
import com.Sucat.domain.friendship.model.FriendShip;
import com.Sucat.domain.friendship.model.FriendshipStatus;
import com.Sucat.domain.friendship.repository.FriendShipQueryRepository;
import com.Sucat.domain.friendship.repository.FriendShipRepository;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.service.NotifyService;
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

import static com.Sucat.domain.user.dto.UserDto.FriendProfileResponse;
import static com.Sucat.global.common.code.ErrorCode.INVALID_FRIENDSHIP_REQUEST_USER;

@Service
@RequiredArgsConstructor
public class FriendShipService {
    private final FriendShipRepository friendShipRepository;
    private final FriendShipQueryRepository friendShipQueryRepository;
    private final UserService userService;
    private final NotifyService notifyService;
    private final JwtUtil jwtUtil;

    /* 친구 요청 */
    @Transactional
    public void createFriendShip(HttpServletRequest request, String toEmail) {
        User fromUser = jwtUtil.getUserFromRequest(request);
        String fromEmail = fromUser.getEmail();


        validateSelfRequest(toEmail, fromEmail);

        if (!checkReverseFriendship(fromEmail, toEmail, fromUser.getNickname())) {
            User toUser = userService.findByEmail(toEmail);
            saveFriendShipRequest(fromUser, toUser);
            notifyService.send(toUser, NotifyType.FRIEND_REQUEST, fromUser.getNickname() + "님이 친구 요청을 보냈습니다.", "/api/v1/friends/received"); // 알림 클릭시 '받은 친구 요청 조회 페이지'로 이동
        }
    }

    /* 친구 요청 목록 조회 */
    public List<WaitingFriendDto> getWaitingFriendList(HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        return friendShipQueryRepository.findPendingFriendShipsByEmail(user.getEmail());
    }

    /* 친구 목록 조회 */
    public List<AcceptFriendDto> getAcceptFriendList(HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        return friendShipQueryRepository.findAcceptFriendShipsByEmail(user.getEmail());
    }

    /* 친구 요청 승인 */
    @Transactional
    public void approveFriendshipRequest(Long friendshipId, HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        FriendShip friendShip = getFriendShipById(friendshipId);

        if (!user.getEmail().equals(friendShip.getUserEmail()) || friendShip.isFrom()) {
            throw new FriendShipException(ErrorCode.FRIENDSHIP_ACCEPT_NOT_ALLOWED);
        }

        acceptFriendship(friendShip, user.getNickname());
    }

    /* 친구 요청 거절 */
    @Transactional
    public void refuseFriendshipRequest(Long friendshipId, HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        FriendShip friendShip = getFriendShipById(friendshipId);

        if (user.getEmail().equals(friendShip.getFriendEmail())) {
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

        validateUnfriendRequest(userEmail, fromFriendShip);

        FriendShip toFriendShip = friendShipRepository.findByFriendEmail(userEmail);
        friendShipRepository.deleteById(fromFriendshipId);
        friendShipRepository.deleteById(toFriendShip.getId());
    }

    /* 친구 프로필 확인 */
    public FriendProfileResponse getFriendProfile(HttpServletRequest request, String friendEmail) {
        String userEmail = userService.getUserInfo(request).getEmail();

        Optional<FriendShip> friendShipOpt = friendShipRepository.findByUserEmailAndFriendEmail(userEmail, friendEmail);
        if (friendShipOpt.isPresent()) {
            FriendShip friendShip = friendShipOpt.get();
            if (friendShip.getStatus().equals(FriendshipStatus.ACCEPT)) {
                return userService.getFriendProfile(friendEmail);
            }
        }

        throw new FriendShipException(ErrorCode.Friendship_NOT_FOUND);
    }

    /* Using Method */
    public FriendShip getFriendShipById(Long id) {
        return friendShipRepository.findById(id)
                .orElseThrow(() -> new FriendShipException(ErrorCode.Friendship_NOT_FOUND));
    }

    private void validateSelfRequest(String toEmail, String fromEmail) {
        if (toEmail.equals(fromEmail)) {
            throw new FriendShipException(ErrorCode.SELF_FRIENDSHIP_REQUEST);
        }
    }

    private void saveFriendShipRequest(User fromUser, User toUser) {
        FriendShip friendShipFrom = createFriendShip(fromUser, toUser.getEmail(), true);
        FriendShip friendShipTo = createFriendShip(toUser, fromUser.getEmail(), false);

        friendShipRepository.save(friendShipFrom);
        friendShipRepository.save(friendShipTo);

        // 각각 친구 리스트에 추가
        fromUser.addFriendShip(friendShipFrom);
        toUser.addFriendShip(friendShipTo);

        // 매칭되는 친구 요청의 아이디를 서로 저장
        friendShipFrom.setCounterpartId(friendShipTo.getId());
        friendShipTo.setCounterpartId(friendShipFrom.getId());
    }

    private FriendShip createFriendShip(User user, String friendEmail, boolean isFrom) {
        return FriendShip.builder()
                .user(user)
                .userEmail(user.getEmail())
                .friendEmail(friendEmail)
                .status(FriendshipStatus.WAITING)
                .isFrom(isFrom)
                .build();
    }

    private boolean checkReverseFriendship(String fromEmail, String toEmail, String fromUserNickname) {
        Optional<FriendShip>  reverseFriendshipOpt = friendShipRepository.findByUserEmailAndFriendEmail(fromEmail, toEmail);

        if (reverseFriendshipOpt.isPresent()){
            FriendShip reverseFriendship = reverseFriendshipOpt.get();

            checkFriendshipAlready(reverseFriendship); // 이미 친구인지, 기존에 보낸적이 있는 요청인지 검증

            if (reverseFriendship.getStatus() == FriendshipStatus.WAITING && !reverseFriendship.isFrom()) {
                // 받은 친구 요청이 존재한다면, 자동으로 친구 수락
                acceptFriendship(reverseFriendship, fromUserNickname);
                return true;
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

    private void acceptFriendship(FriendShip reverseFriendship, String fromUserNickname) {
        reverseFriendship.acceptRequest();
        friendShipRepository.save(reverseFriendship);

        FriendShip counterpart = getFriendShipById(reverseFriendship.getCounterpartId());
        counterpart.acceptRequest();
        friendShipRepository.save(counterpart);

        notifyService.send(counterpart.getUser(), NotifyType.FRIEND_ACCEPTED, fromUserNickname + "님이 친구 요청을 수락했습니다.", "/api/v1/friends");
    }

    private void validateUnfriendRequest(String userEmail, FriendShip fromFriendShip) {
        if (!fromFriendShip.getUserEmail().equals(userEmail) ||
                fromFriendShip.getStatus().equals(FriendshipStatus.WAITING)) {
            throw new FriendShipException(INVALID_FRIENDSHIP_REQUEST_USER);
        }
    }

    public void validateFriendship(String userEmail, String friendEmail) {
        Optional<FriendShip> friendShipOpt = friendShipRepository.findByUserEmailAndFriendEmail(userEmail, friendEmail);
        if (friendShipOpt.isPresent()) {
            FriendShip friendShip = friendShipOpt.get();
            if (!friendShip.getStatus().equals(FriendshipStatus.ACCEPT)) {
                throw new FriendShipException(ErrorCode.Friendship_NOT_FOUND);
            }
        } else {
            throw new FriendShipException(ErrorCode.Friendship_NOT_FOUND);
        }
    }

}

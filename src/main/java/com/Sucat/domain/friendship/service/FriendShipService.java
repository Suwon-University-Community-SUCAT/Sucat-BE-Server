package com.Sucat.domain.friendship.service;

import com.Sucat.domain.friendship.dto.FriendListResponse;
import com.Sucat.domain.friendship.dto.WaitingFriendDto;
import com.Sucat.domain.friendship.exception.FriendShipException;
import com.Sucat.domain.friendship.model.FriendShip;
import com.Sucat.domain.friendship.model.FriendshipStatus;
import com.Sucat.domain.friendship.repository.FriendShipQueryRepository;
import com.Sucat.domain.friendship.repository.FriendShipRepository;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.service.NotifyService;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.Sucat.domain.friendship.dto.FriendShipDto.WaitingFriendWithTotalCountResponse;
import static com.Sucat.global.common.code.ErrorCode.INVALID_FRIENDSHIP_REQUEST_USER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendShipService {
    private final FriendShipRepository friendShipRepository;
    private final FriendShipQueryRepository friendShipQueryRepository;
    private final NotifyService notifyService;

    /* 친구 요청 */
    @Transactional
    public void createFriendShip(User fromUser, User toUser) {
        validateSelfRequest(fromUser, toUser);

        if (!checkReverseFriendship(fromUser.getEmail(), toUser.getEmail(), fromUser.getNickname())) {
            saveFriendShipRequest(fromUser, toUser);
        }
    }

    /* 친구 요청 목록 조회 */
    public WaitingFriendWithTotalCountResponse getWaitingFriendList(User user, String sortKey) {
        List<WaitingFriendDto> pendingFriendShipsByEmail = friendShipQueryRepository.findPendingFriendShipsByEmail(user.getEmail(), sortKey);
        int totalCount = pendingFriendShipsByEmail.size();

        return WaitingFriendWithTotalCountResponse.of(pendingFriendShipsByEmail, totalCount);
    }

    /* 친구 목록 조회 */
    public Page<FriendListResponse> getAcceptFriendList(User user, int page, int size, String sortKey) {
        Pageable pageable = pagingCondition(page, size, sortKey);

        return friendShipRepository.findAcceptFriendShipsByEmail(user.getEmail(), FriendshipStatus.ACCEPT, pageable);
    }

    /* 친구 요청 승인 */
    @Transactional
    public void approveFriendshipRequest(Long friendshipId, User user) {
        FriendShip friendShip = getFriendShipById(friendshipId);

        if (!user.getEmail().equals(friendShip.getUserEmail()) || friendShip.isFrom()) {
            throw new FriendShipException(ErrorCode.FRIENDSHIP_ACCEPT_NOT_ALLOWED);
        }

        acceptFriendship(friendShip, user.getNickname());
    }

    /* 친구 요청 거절 */
    @Transactional
    public void refuseFriendshipRequest(Long friendshipId, User user) {
        FriendShip friendShip = getFriendShipById(friendshipId);

        if (user.getEmail().equals(friendShip.getFriendEmail())) {
            throw new FriendShipException(ErrorCode.FRIENDSHIP_DECLINE_NOT_ALLOWED);
        }

        friendShipRepository.deleteById(friendShip.getId());
        friendShipRepository.deleteById(friendShip.getCounterpartId());
    }

    /* 친구 삭제 */
    @Transactional
    public void unfriend(User currentUser, Long fromFriendshipId) {
        String userEmail = currentUser.getEmail();
        FriendShip fromFriendShip = getFriendShipById(fromFriendshipId);

        validateUnfriendRequest(userEmail, fromFriendShip);

        FriendShip toFriendShip = friendShipRepository.findByFriendEmail(userEmail);
        friendShipRepository.deleteById(fromFriendshipId);
        friendShipRepository.deleteById(toFriendShip.getId());
    }

    /* 친구 프로필 확인 */
    public String getFriendProfile(User currentUser, String friendEmail) {
        String userEmail = currentUser.getEmail();

        Optional<FriendShip> friendShipOpt = friendShipRepository.findByUserEmailAndFriendEmail(userEmail, friendEmail);
        if (friendShipOpt.isPresent()) {
            FriendShip friendShip = friendShipOpt.get();
            if (friendShip.getStatus().equals(FriendshipStatus.ACCEPT)) {
                return friendEmail;
            }
        }

        throw new FriendShipException(ErrorCode.Friendship_NOT_FOUND);
    }

    /* 친구 검색 메서드 */
    public List<FriendListResponse> getSearchFriend(final String keyword, final String sortKey, User currentUser) {

        return friendShipQueryRepository.getSearchFriend(keyword, sortKey, currentUser);
    }


    /* Using Method */
    public FriendShip getFriendShipById(Long id) {
        return friendShipRepository.findById(id)
                .orElseThrow(() -> new FriendShipException(ErrorCode.Friendship_NOT_FOUND));
    }

    private void validateSelfRequest(User fromUser, User toUser) {
        if (toUser.equals(fromUser)) {
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

    private Pageable pagingCondition(int page, int size, String sortKey) {
        Sort sort;
        switch (sortKey) {
            case "createdAsc":
                sort = Sort.by(Sort.Direction.ASC, "createdAt");
                break;
            case "name":
                sort = Sort.by(Sort.Direction.ASC, "name"); // 이름순 정렬 추가
                break;
            default: // 최신순
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
                break;
        }
        return PageRequest.of(page, size, sort);
    }

}

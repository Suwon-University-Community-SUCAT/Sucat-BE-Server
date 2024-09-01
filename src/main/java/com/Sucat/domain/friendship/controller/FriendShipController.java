package com.Sucat.domain.friendship.controller;

import com.Sucat.domain.friendship.dto.FriendListResponse;
import com.Sucat.domain.friendship.dto.FriendShipDto;
import com.Sucat.domain.friendship.service.FriendShipService;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.service.NotifyService;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.annotation.CurrentUser;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.Sucat.domain.user.dto.UserDto.FriendProfileResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendShipController {
    private final FriendShipService friendShipService;
    private final UserService userService;
    private final NotifyService notifyService;

    /* 친구 요청 전송 */
    @PostMapping("/{email}")
    public ResponseEntity<ApiResponse<Object>> sendFriendShipRequest(@PathVariable(name = "email") String email, @CurrentUser User fromUser) {
        User toUser = userService.findByEmail(email);
        friendShipService.createFriendShip(fromUser, toUser);

        notifyService.send(
                toUser,
                NotifyType.FRIEND_REQUEST,
                fromUser.getNickname() + "님이 친구 요청을 보냈습니다.",
                "/api/v1/friends/received");

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 받은 친구 요청 조회 */
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Object>> getWaitingFriendInfo(
            @CurrentUser User user,
            @RequestParam(name = "sortKey", defaultValue = "createdAtDesc") @Nullable final String sortKey
            ) {

        FriendShipDto.WaitingFriendWithTotalCountResponse waitingFriendList = friendShipService.getWaitingFriendList(user, sortKey);
        return ApiResponse.onSuccess(SuccessCode._OK, waitingFriendList);
    }

    /* 친구 수락 */
    @PostMapping("/approve/{friendshipId}")
    public ResponseEntity<ApiResponse<Object>> approveFriendShip(@PathVariable(name = "friendshipId") Long friendshipId,
                                                                 @CurrentUser User user) {
        friendShipService.approveFriendshipRequest(friendshipId, user);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 친구 요청 취소, 거절 */
    @PostMapping("/refuse/{friendshipId}")
    public ResponseEntity<ApiResponse<Object>> refuseFriendShip(@PathVariable(name = "friendshipId") Long friendshipId,
                                                                @CurrentUser User user) {
        friendShipService.refuseFriendshipRequest(friendshipId, user);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 친구 삭제 */
    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<ApiResponse<Object>> unfriend(@PathVariable("friendshipId") Long friendshipId,
                                                        @CurrentUser User user) {

        friendShipService.unfriend(user, friendshipId);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 친구 목록 */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getFriendList(
            @CurrentUser User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(name = "sortKey", defaultValue = "createdAtDesc") @Nullable final String sortKey) {
        Page<FriendListResponse> acceptFriendList = friendShipService.getAcceptFriendList(user, page, size, sortKey);
        return ApiResponse.onSuccess(SuccessCode._OK, acceptFriendList);
    }

    /* 친구 프로필 확인 */
    @GetMapping("/profile/{email}")
    public ResponseEntity<ApiResponse<Object>> getFriendProfile(@CurrentUser User user, @PathVariable("email") String email) {

        String friendEmail = friendShipService.getFriendProfile(user, email);
        FriendProfileResponse friendProfile = userService.getFriendProfile(friendEmail);

        return ApiResponse.onSuccess(SuccessCode._OK, friendProfile);
    }

    /* 친구 검색 */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> friendSearch(
            @RequestParam(name = "keyword", defaultValue = "") @Nullable String keyword,
            @RequestParam(name = "sortKey", defaultValue = "name") @Nullable final String sortKey,
            @CurrentUser User user
    ) {
        List<FriendListResponse> friendSearchResponses = friendShipService.getSearchFriend(keyword, sortKey, user);

        return ApiResponse.onSuccess(SuccessCode._OK, friendSearchResponses);
    }
}

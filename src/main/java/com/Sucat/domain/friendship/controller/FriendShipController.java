package com.Sucat.domain.friendship.controller;

import com.Sucat.domain.friendship.dto.FriendListResponse;
import com.Sucat.domain.friendship.dto.FriendShipDto;
import com.Sucat.domain.friendship.service.FriendShipService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    /* 친구 요청 전송 */
    @PostMapping("/{email}")
    public ResponseEntity<ApiResponse<Object>> sendFriendShipRequest(@PathVariable(name = "email") String email, HttpServletRequest request) {
        friendShipService.createFriendShip(request, email);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 받은 친구 요청 조회 */
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<Object>> getWaitingFriendInfo(HttpServletRequest request) {
        FriendShipDto.WaitingFriendWithTotalCountResponse waitingFriendList = friendShipService.getWaitingFriendList(request);
        return ApiResponse.onSuccess(SuccessCode._OK, waitingFriendList);
    }

    /* 친구 수락 */
    @PostMapping("/approve/{friendshipId}")
    public ResponseEntity<ApiResponse<Object>> approveFriendShip(@PathVariable(name = "friendshipId") Long friendshipId, HttpServletRequest request) {
        friendShipService.approveFriendshipRequest(friendshipId, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 친구 요청 취소, 거절 */
    @PostMapping("/refuse/{friendshipId}")
    public ResponseEntity<ApiResponse<Object>> refuseFriendShip(@PathVariable(name = "friendshipId") Long friendshipId, HttpServletRequest request) {
        friendShipService.refuseFriendshipRequest(friendshipId, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 친구 삭제 */
    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<ApiResponse<Object>> unfriend(@PathVariable("friendshipId") Long friendshipId,HttpServletRequest request) {

        friendShipService.unfriend(request, friendshipId);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 친구 목록 */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getFriendList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(name = "sortKey", defaultValue = "createAtDesc") @Nullable final String sortKey, Sort sort) {
        List<FriendListResponse> acceptFriendList = friendShipService.getAcceptFriendList(request, page, size, sortKey);
        return ApiResponse.onSuccess(SuccessCode._OK, acceptFriendList);
    }

    /* 친구 프로필 확인 */
    @GetMapping("/profile/{email}")
    public ResponseEntity<ApiResponse<Object>> getFriendProfile(HttpServletRequest request, @PathVariable("email") String email) {
        FriendProfileResponse friendProfile = friendShipService.getFriendProfile(request, email);

        return ApiResponse.onSuccess(SuccessCode._OK, friendProfile);
    }

    /* 친구 검색 */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> friendSearch(
            @RequestParam(name = "keyword", defaultValue = "") @Nullable String keyword,
            @PageableDefault(page = 0, size = 30) @Nullable final Pageable pageable,
            @RequestParam(name = "sortKey", defaultValue = "name") @Nullable final String sortKey,
            HttpServletRequest request
    ) {
        List<FriendListResponse> friendSearchResponses = friendShipService.getSearchFriend(keyword, pageable, sortKey, request);

        return ApiResponse.onSuccess(SuccessCode._OK, friendSearchResponses);
    }
}

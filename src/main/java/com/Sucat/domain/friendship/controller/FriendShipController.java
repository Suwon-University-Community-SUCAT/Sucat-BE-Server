package com.Sucat.domain.friendship.controller;

import com.Sucat.domain.friendship.dto.AcceptFriendDto;
import com.Sucat.domain.friendship.dto.WaitingFriendDto;
import com.Sucat.domain.friendship.service.FriendShipService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FriendShipController {
    private final FriendShipService friendShipService;

    /* 친구 요청 전송 */
    @PostMapping("/friends/{email}")
    public ResponseEntity<ApiResponse<Object>> sendFriendShipRequest(@PathVariable(name = "email") String email, HttpServletRequest request) {
        friendShipService.createFriendShip(request, email);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 받은 친구 요청 조회 */
    @GetMapping("/friends/received")
    public ResponseEntity<ApiResponse<Object>> getWaitingFriendInfo(HttpServletRequest request) {
        List<WaitingFriendDto> waitingFriendList = friendShipService.getWaitingFriendList(request);
        return ApiResponse.onSuccess(SuccessCode._OK, waitingFriendList);
    }

    /* 친구 수락 */
    @PostMapping("/friends/approve/{friendshipId}")
    public ResponseEntity<ApiResponse<Object>> approveFriendShip(@PathVariable(name = "friendshipId") Long friendshipId, HttpServletRequest request) {
        friendShipService.approveFriendshipRequest(friendshipId, request);
        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 친구 목록 */
    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<Object>> getFriendList(HttpServletRequest request) {
        List<AcceptFriendDto> acceptFriendList = friendShipService.getAcceptFriendList(request);
        return ApiResponse.onSuccess(SuccessCode._OK, acceptFriendList);
    }
}

package com.Sucat.domain.friendship.controller;

import com.Sucat.domain.friendship.service.FriendShipService;
import com.Sucat.global.common.code.SuccessCode;
import com.Sucat.global.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

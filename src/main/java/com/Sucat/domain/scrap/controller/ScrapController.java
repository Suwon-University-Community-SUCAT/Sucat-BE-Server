package com.Sucat.domain.scrap.controller;


import com.Sucat.domain.board.dto.BoardDto;
import com.Sucat.domain.scrap.service.ScrapService;
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
@RequestMapping("/api/v1/scrap")
public class ScrapController {
    private final ScrapService scrapService;

    /* 스크랩 하기, 취소하기 */
    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Object>> scrap(
            @PathVariable Long boardId,
            HttpServletRequest request) {
        scrapService.scrap(boardId, request);

        return ApiResponse.onSuccess(SuccessCode._OK);
    }

    /* 내가 스크랩한 게시물 불러오기 */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> myScrap(
            HttpServletRequest request
    ) {
        List<BoardDto.BoardListResponse> myScrap = scrapService.getMyScrap(request);
        return ApiResponse.onSuccess(SuccessCode._OK, myScrap);
    }
}

package com.Sucat.domain.board.controller;

import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.service.BoardService;
import com.Sucat.domain.token.repository.BlacklistedTokenRepository;
import com.Sucat.domain.token.repository.TokenRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import com.Sucat.global.common.code.SuccessCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.Sucat.domain.board.dto.BoardDto.BoardPostRequest;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
//@WebMvcTest(BoardController.class)
@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {
    /**
     * 웹 API 테스트 시 사용
     * Spring MVC Test의 시작점
     * 웹 서버를 띄우지 않고도 스프링 MVC (DispatcherServlet)가 요청을 처리하는 과정을 확인할 수 있음
     */
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    @MockBean
    private UserService userService;

    @MockBean
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @MockBean
    private TokenRepository tokenRepository;

    @Test
    @WithMockUser(username = "test1111@suwon.ac.kr", authorities = "USER")
    @DisplayName("게시글 작성 - 성공")
    void createBoardTest() throws Exception {

        // given
        BoardPostRequest request = new BoardPostRequest("제목", "내용", BoardCategory.FREE);
        MockMultipartFile requestFile = new MockMultipartFile("request", "", "application/json", objectMapper.writeValueAsBytes(request));
        MockMultipartFile imageFile = new MockMultipartFile("images", "image.jpg", "image/jpeg", "test-image".getBytes());

        // when
        doNothing().when(boardService).createBoard(any(), any(), any());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/boards")
                        .file(requestFile)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(SuccessCode._CREATED.getCode()));

//        // verify
        verify(boardService, times(1)).createBoard(any(), any(), anyList());

        // verify
        ArgumentCaptor<Board> boardCaptor = forClass(Board.class);
        ArgumentCaptor<User> userCaptor = forClass(User.class);
        ArgumentCaptor<List<MultipartFile>> imagesCaptor = forClass(List.class);

        verify(boardService, times(1)).createBoard(boardCaptor.capture(), userCaptor.capture(), imagesCaptor.capture());

        // 검증할 값
        Board capturedBoard = boardCaptor.getValue();
        List<MultipartFile> capturedImages = imagesCaptor.getValue();

        assertEquals("제목", capturedBoard.getTitle());
        assertEquals("내용", capturedBoard.getContent());
        assertEquals(BoardCategory.FREE, capturedBoard.getCategory());
        assertNotNull(capturedImages);
        assertEquals(1, capturedImages.size());
        assertEquals("image.jpg", capturedImages.get(0).getOriginalFilename());
    }
}

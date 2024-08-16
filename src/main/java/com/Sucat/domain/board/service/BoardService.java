package com.Sucat.domain.board.service;

import com.Sucat.domain.board.dto.BoardPostRequestDTO;
import com.Sucat.domain.board.dto.BoardUpdateRequestDTO;
import com.Sucat.domain.board.dto.ResponseDTO;
import com.Sucat.domain.comment.dto.CommentPostResponse;
import com.Sucat.domain.board.model.Board;
import com.Sucat.domain.board.dto.BoardResponse;
import com.Sucat.domain.board.model.BoardCategory;
import com.Sucat.domain.board.repository.BoardRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;

    @Transactional
    public void createBoard(BoardPostRequestDTO requestDTO, HttpServletRequest request) {
        User user = userService.getUserInfo(request);
        Board board = new Board(user.getName(), requestDTO.getTitle(), requestDTO.getContent(), requestDTO.getCategory());
        board.addUser(user);
        user.addBoard(board);
    }

    @Transactional
    public void updateBoard(Long id, BoardUpdateRequestDTO requestDTO, HttpServletRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        User user = userService.getUserInfo(request);

        // 게시글 작성자만 수정 가능
        if (!board.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to update this board");
        }

        board.updateBoard(requestDTO.getTitle(), requestDTO.getContent());
    }

    @Transactional
    public void deleteBoard(Long id, HttpServletRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        User user = userService.getUserInfo(request);

        // 게시글 작성자만 삭제 가능
        if (!board.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized to delete this board");
        }

        boardRepository.delete(board);
    }

    //특정 카테고리의 게시글 목록 조회
    public ResponseDTO getAllBoards(BoardCategory category) {
        List<BoardResponse> posts = boardRepository.findByCategory(category).stream()
                .map(board -> new BoardResponse(
                        board.getMinute().toString(),
                        //board.getImages().stream().map(image -> image.getUrl()).collect(Collectors.toList()),
                        board.getTitle(),
                        board.getContent(),
                        board.getUser().getName(),
                        board.getLikeCount(),
                        board.getCommentCount(),
                        board.getScrapCount()
                        //board.getCategory()
                ))
                .collect(Collectors.toList());
        //TODO 쿼리 최적화 필요
        //핫포스트
        Board hotPost = boardRepository.findAll().stream()
                .max((a, b) -> Integer.compare(a.getLikeCount(), b.getLikeCount()))
                .orElseThrow(() -> new RuntimeException("No hotPost found"));

        ResponseDTO.HotPostResponse hotPostResponse = new ResponseDTO.HotPostResponse(
                hotPost.getTitle(), hotPost.getLikeCount()
        );

        return new ResponseDTO(posts, hotPostResponse);
    }

    public BoardResponse getBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("Board not found"));
        List<CommentPostResponse> comments = board.getComments().stream()
                .map(comment -> new CommentPostResponse(
                        comment.getUser().getName(),
                        comment.getContent(),
                        comment.getMinute().toString(),
                        comment.getLikeCount(),
                        comment.getCommentCount(),
                        comment.getScrapCount()
                        //comment.getUser().getImageUrl() // Assuming User has an imageUrl field
                ))
                .collect(Collectors.toList());

        BoardResponse boardResponse = new BoardResponse(
                board.getMinute().toString(),
                board.getTitle(),
                board.getContent(),
                board.getUser().getName(),
                board.getLikeCount(),
                board.getCommentCount(),
                board.getScrapCount()
                //board.getCategory()
        );
        boardResponse.setComments(comments);
        return boardResponse;
    }
}
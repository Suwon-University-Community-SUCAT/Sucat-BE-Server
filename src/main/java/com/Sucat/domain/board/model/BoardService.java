package com.Sucat.domain.board.model;

import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;

    @Autowired
    public BoardService(BoardRepository boardRepository, UserService userService) {
        this.boardRepository = boardRepository;
        this.userService = userService;
    }


    public Board createBoard(String name, String title, String content, BoardCategory category, HttpServletRequest request) {
        User user = userService.getUserInfo(request);
        Board board = new Board(name, title, content, category, user);
        return boardRepository.save(board);
    }

    public ResponseDTO getAllBoards() {
        List<BoardResponse> posts = boardRepository.findAll().stream()
                .map(board -> new BoardResponse(
                        board.getMinute().toString(),
                        //board.getImages().stream().map(image -> image.getUrl()).collect(Collectors.toList()),
                        board.getTitle(),
                        board.getContent(),
                        board.getUser().getName(),
                        board.getLikeCount(),
                        board.getCommentCount(),
                        board.getScrapCount()
                ))
                .collect(Collectors.toList());
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
        );
        boardResponse.setComments(comments);
        return boardResponse;
    }
}
package com.Sucat.domain.board.DTO;

import com.Sucat.domain.board.model.BoardCategory;
import lombok.Data;


@Data
public class BoardPostRequestDTO {
    private String title;
    private String content;
    private BoardCategory category;
}

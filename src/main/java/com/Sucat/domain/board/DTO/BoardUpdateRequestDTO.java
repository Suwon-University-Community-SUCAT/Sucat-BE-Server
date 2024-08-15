package com.Sucat.domain.board.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateRequestDTO {

    @NotNull
    private String title;

    @NotNull
    private String content;
}

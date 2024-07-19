package com.Sucat.domain.chatmessage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto implements Serializable {
    private static final long serialVersionUID = 2082503192322391880L;
    @NotNull
    private String roomId;
    @NotNull
    private Long senderId;
    @NotBlank
    private String content;

    public void setContent(String content) {
        this.content = content;
    }
}
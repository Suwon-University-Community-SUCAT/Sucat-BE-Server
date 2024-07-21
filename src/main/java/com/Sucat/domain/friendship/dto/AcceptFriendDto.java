package com.Sucat.domain.friendship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcceptFriendDto {
    private Long friendshipId;
    private String friendEmail;
    private String friendNickname;
    private String department;
    private String info;
}

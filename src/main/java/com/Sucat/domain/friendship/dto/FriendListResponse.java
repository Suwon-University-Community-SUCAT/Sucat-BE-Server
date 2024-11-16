package com.Sucat.domain.friendship.dto;

import com.Sucat.domain.user.model.Department;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendListResponse {
    private Long friendshipId;
    private String friendEmail;
    private String friendNickname;
    private Department department;
    private String intro;
    private String profileImageUrl;
}

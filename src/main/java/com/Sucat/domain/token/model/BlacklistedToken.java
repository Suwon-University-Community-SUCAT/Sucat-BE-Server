package com.Sucat.domain.token.model;

import com.Sucat.global.common.dao.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken extends BaseEntity {
    @Id
    private String token;
}
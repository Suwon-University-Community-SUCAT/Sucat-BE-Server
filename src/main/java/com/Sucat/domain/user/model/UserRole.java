package com.Sucat.domain.user.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    USER, ADMIN;


    @Override
    public String getAuthority() {
        return name();
    }
}

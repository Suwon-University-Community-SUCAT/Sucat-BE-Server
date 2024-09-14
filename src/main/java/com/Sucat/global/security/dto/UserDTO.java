package com.Sucat.global.security.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String password;
    private String role;

    public UserDTO(String email, String role) {
        this.email = email;
        this.role = role;
    }

    public UserDTO(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

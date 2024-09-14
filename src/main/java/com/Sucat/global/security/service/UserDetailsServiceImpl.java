package com.Sucat.global.security.service;

import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.security.CustomUserDetails;
import com.Sucat.global.security.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username).get();

        if (user != null) {
            UserDTO userDTO = new UserDTO(user.getEmail(), user.getPassword(), user.getRole().toString());

            return new CustomUserDetails(userDTO);
        }

        return null;
    }
}

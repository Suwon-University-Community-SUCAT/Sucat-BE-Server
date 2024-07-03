package com.Sucat.domain.user.service;

import com.Sucat.domain.user.exception.UserException;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void join(User user) {
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    public void emailDuplicateVerification(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    public void nicknameDuplicateVerification(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent())  {
            throw new UserException(ErrorCode.NICKNAME_DUPLICATION);
        }
    }

}

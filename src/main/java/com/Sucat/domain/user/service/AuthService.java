package com.Sucat.domain.user.service;

import com.Sucat.domain.user.exception.UserException;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.Sucat.domain.user.dto.UserDto.UserTermAgree;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public void signup(User user) {
        userService.emailDuplicateVerification(user.getEmail());
        userService.validatePassword(user.getPassword());
        userService.nicknameDuplicateVerification(user.getNickname());//이미 회원가입 과정에서 닉네임 중복 검사를 하지만 동시성 에러를 고려
        user.updatePassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
    }

    // 모든 약관이 동의되었는지 확인하는 메서드
    public void AllAgreementsAccepted(UserTermAgree userTermAgree) {
        List<Boolean> agreements = userTermAgree.agreements();
        for (Boolean agreement : agreements) {
            if (!agreement) {
                throw new UserException(ErrorCode.TERMS_NOT_ACCEPTED);
            }
        }
    }

}

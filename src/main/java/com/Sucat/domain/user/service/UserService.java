package com.Sucat.domain.user.service;

import com.Sucat.domain.user.exception.UserException;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.Sucat.domain.user.dto.UserDto.PasswordResetRequest;
import static com.Sucat.domain.user.dto.UserDto.UserProfileUpdateRequest;
import static com.Sucat.global.common.constant.ConstraintConstants.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 비밀번호 암호화 메서드
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    public User getUserInfo(HttpServletRequest request) {
        return jwtUtil.getUserFromRequest(request);
    }

    @Transactional
    public void updateProfile(HttpServletRequest request, UserProfileUpdateRequest userProfileUpdateRequest) {
        User user = jwtUtil.getUserFromRequest(request);

        // 닉네임 중복 검사
        nicknameDuplicateVerification(userProfileUpdateRequest.nickname());

        user.updateProfile(userProfileUpdateRequest.nickname());
    }

    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        User currentUser = findByEmail(passwordResetRequest.email());
        String resetPassword = passwordResetRequest.password();

        validatePassword(resetPassword);
        currentUser.resetPassword(passwordEncoder.encode(resetPassword));
    }

//    @Transactional
//    public void chageProfile(HttpServletRequest request, UserProfileUpdateRequest userProfileUpdateRequest) {
//
//    }

    // 비밀번호 유효성 검사 메서드
    public void validatePassword(String password) {
        // 비밀번호 만료 날짜 설정, 이전 비밀번호와의 비교 등 정책 추가 고민
        if (password == null || password.isEmpty()) {
            throw new UserException(ErrorCode.PASSWORD_MISSING_OR_EMPTY);
        }
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new UserException(ErrorCode.PASSWORD_LENGTH_INVALID);
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            throw new UserException(ErrorCode.PASSWORD_COMPLEXITY_REQUIRED);
        }
    }
}

package com.Sucat.domain.user.service;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.image.service.ImageService;
import com.Sucat.domain.user.exception.UserException;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.repository.UserQueryRepository;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.Sucat.domain.user.dto.UserDto.*;
import static com.Sucat.global.common.code.ErrorCode.*;
import static com.Sucat.global.common.constant.ConstraintConstants.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;
    private final JwtUtil jwtUtil;
    private final ImageService imageService;

    // 비밀번호 암호화 메서드
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /* 회원 프로필 업데이트 메서드 */
    @Transactional
    public void updateProfile(User user, UserProfileUpdateRequest userProfileUpdateRequest, MultipartFile image) throws IOException {
        // 닉네임이 null이 아니고 빈 값이 아니면 닉네임 변경 로직 수행
        if (userProfileUpdateRequest.nickname() != null && !userProfileUpdateRequest.nickname().isEmpty()) {
            // 닉네임 중복 검사
            nicknameDuplicateVerification(userProfileUpdateRequest.nickname());
            user.updateNickname(userProfileUpdateRequest.nickname());
        }

        // 한줄 소개가 null이 아니고 빈 값이 아니면 한줄 소개 변경 로직 수행
        if (userProfileUpdateRequest.intro() != null && !userProfileUpdateRequest.intro().isEmpty()) {
            user.updateIntro(userProfileUpdateRequest.intro());
        }

        updateUserImage(image, user);
    }

    /* 비밀번호 찾기 메서드 */
    @Transactional
    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        User currentUser = findByEmail(passwordResetRequest.email());
        String resetPassword = passwordResetRequest.password();

        validatePassword(resetPassword);
        currentUser.updatePassword(passwordEncoder.encode(resetPassword));
    }

    /* 비밀번호 변경 메서드 */
    @Transactional
    public void changePassword(User user, UserPasswordUpdateRequest userPasswordUpdateRequest) {

        String encodedPassword = user.getPassword(); // 이미 인코딩된 비밀번호
        String currentPassword = userPasswordUpdateRequest.currentPassword();
        String newPassword = userPasswordUpdateRequest.newPassword();

        // 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(currentPassword, encodedPassword)) {
            throw new UserException(USER_MISMATCH);
        }

        // 새 비밀번호 유효성 검사
        validatePassword(newPassword);

        // 새 비밀번호로 업데이트
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    /* 회원 프로필 불러오기 메서드 */
    public UserProfileResponse getUserProfile(User user) {
        return UserProfileResponse.of(user);
    }

    /* 친구 프로필 불러오기 메서드 */
    public FriendProfileResponse getFriendProfile(String email) {
        User user = userQueryRepository.findUserProfileByEmail(email);
        return FriendProfileResponse.of(user);
    }

    /* Using Method */
    private void updateUserImage(MultipartFile image, User user) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imageName = imageService.storeFile(image);

            Image userImage = user.getUserImage(); // 기존의 사용자 이미지 객체를 가져옴

            if (userImage == null) {
                // 만약 기존 이미지가 없다면 새로운 이미지 객체 생성
                userImage = Image.ofUser(user, imageName);
                user.updateUserImage(userImage);
            } else {
                // 기존 이미지 객체의 URL만 변경
                userImage.updateImageUrl(imageName);
            }
        }
    }

    // 비밀번호 유효성 검사 메서드
    public void validatePassword(String password) {
        // 비밀번호 만료 날짜 설정, 이전 비밀번호와의 비교 등 정책 추가 고민
        if (password == null || password.isEmpty()) {
            throw new UserException(PASSWORD_MISSING_OR_EMPTY);
        }
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new UserException(PASSWORD_LENGTH_INVALID);
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            throw new UserException(PASSWORD_COMPLEXITY_REQUIRED);
        }
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
    }

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void emailDuplicateVerification(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException(USER_ALREADY_EXISTS);
        }
    }

    public void nicknameDuplicateVerification(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent())  {
            throw new UserException(NICKNAME_DUPLICATION);
        }
    }

    public User getUserInfo(HttpServletRequest request) {
        return jwtUtil.getUserFromRequest(request);
    }
}

package com.Sucat.domain.user.service;

import com.Sucat.domain.image.model.Image;
import com.Sucat.domain.image.service.S3Uploader;
import com.Sucat.domain.user.exception.UserException;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final S3Uploader s3Uploader;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /* 회원가입 메서드 */
    @Transactional
    public void signup(User user, MultipartFile image) throws IOException {
        userService.emailDuplicateVerification(user.getEmail());
        userService.validatePassword(user.getPassword());
        userService.nicknameDuplicateVerification(user.getNickname());//이미 회원가입 과정에서 닉네임 중복 검사를 하지만 동시성 에러를 고려

        userService.save(user);
        user.updateRole();
        user.updatePassword(passwordEncoder.encode(user.getPassword()));

        if (image != null && !image.isEmpty()) {
            Map<String, String> imageInfo = s3Uploader.upload(image);
            Image userImage = Image.ofUser(user, imageInfo.get("imageUrl"), imageInfo.get("imageName"));
            user.updateUserImage(userImage);
        }
    }

    /* Using Method */
    // 모든 약관이 동의되었는지 확인하는 메서드
    public void AllAgreementsAccepted(int term1, int term2, int term3, int term4) {
        // 하나라도 0이면 예외를 던집니다.
        if (term1 == 0 || term2 == 0 || term3 == 0 || term4 == 0) {
            throw new UserException(ErrorCode.TERMS_NOT_ACCEPTED);
        }
    }


}

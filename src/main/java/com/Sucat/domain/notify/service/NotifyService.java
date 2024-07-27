package com.Sucat.domain.notify.service;

import com.Sucat.domain.notify.model.Notify;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.repository.EmitterRepository;
import com.Sucat.domain.notify.repository.NotifyRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotifyService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotifyRepository notifyRepository;
    private final EmitterRepository emitterRepository;
    private final JwtUtil jwtUtil;

    public SseEmitter subscribe(HttpServletRequest request, String lastEventId) {
        String email = jwtUtil.getEmailFromRequest(request);
        String emitterId = makeTimeIncludeId(email); // email을 포함한 SseEmitter을 식별하기 위한 고유 아이디 생성
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId)); // SseEmitter가 완료될 때 실행될 콜백을 등록
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId)); // Sse가 타임아웃될 때 실행될 콜백을 등록

        //503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(email);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [email = " + email + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, email, emitterId, emitter);
        }

        return emitter;
    }

    private String makeTimeIncludeId(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    // 클라이언트에게 알림 전송
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId) // 잔송할 이벤트의 ID
                    .name("sse")
                    .data(data)
            );
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
        }
    }

    // 주어진 lastEventId가 비어있는지 확인
    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    // 클라이언트가 마지막으로 수산한 이벤트 이후의 데이터를 찾아서 클라이언트에 전송
    private void sendLostData(String lastEventId, String email, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(email); // 이메일에 연관된 모든 이벤트 캐시를 가져온다.
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public void send(User receiver, NotifyType notifyType, String content, String url) {
        Notify notify = notifyRepository.save(createNotify(receiver, notifyType, content, url));

        String receiverEmail = receiver.getEmail();
        String eventId = receiverEmail + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiverEmail);
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notify);
                    sendNotification(emitter, eventId, key, );
                }
        );
    }

    private Notify createNotify(User receiver, NotifyType notifyType, String content, String url) {
        return Notify.builder()
                .user(receiver)
                .notifyType(notifyType)
                .content(content)
                .url(url)
                .isRead(false)
                .build();
    }


}

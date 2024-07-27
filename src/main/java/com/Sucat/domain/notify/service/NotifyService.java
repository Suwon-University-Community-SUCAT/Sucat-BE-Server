package com.Sucat.domain.notify.service;

import com.Sucat.domain.notify.model.Notify;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.repository.EmitterRepository;
import com.Sucat.domain.notify.repository.NotifyRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
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
        log.info("new emitter added : {}", emitter);
        log.info("lastEventId : {}", lastEventId);

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId)); // SseEmitter가 완료될 때 실행될 콜백을 등록
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId)); // Sse가 타임아웃될 때 실행될 콜백을 등록
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        /* 503 Service Unavailable 방지용 Dummy event 전송 */
        String eventId = makeTimeIncludeId(email);
        sendNotification(emitter, eventId, "EventStream Created. [userEmail=" + email + "]");

        /* client가 미수신한 event 목록이 존재하는 경우 */
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, email, emitterId, emitter);
        }

        return emitter;
    }

    /* [SSE 통신] specific user에게 알림 전송 */
    public void send(User receiver, NotifyType notifyType, String content, String url) {
        Notify notify = notifyRepository.save(createNotify(receiver, notifyType, content, url));

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiver.getEmail());
        emitters.forEach(
                (key, emitter) -> {
                    log.info("key, notify : {}, {}", key, notify);
                    emitterRepository.saveEventCache(key, notify); // 저장
                    emitEventToClient(emitter, key, notify); // 전송
                }
        );
    }

    /* [SSE 통신] dummy data 생성
    *  : 503 Service Unavailable 방지
    * */
    private void sendNotification(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            handleSendError(emitter, emitterId, exception);
        }
    }

    private String makeTimeIncludeId(String email) {
        return email + "_" + System.currentTimeMillis();
    }

    /* [SSE 통신] */
    private void emitEventToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            send(emitter, emitterId, data);
            emitterRepository.deleteById(emitterId);

        } catch (Exception e) {
            handleSendError(emitter, emitterId, e);
            throw new RuntimeException("Connection Failed.");
        }
    }

    /**
     * [SSE 통신] notification type별 event 전송
     * 실질적으로 알림을 전송하는 메서드
     */
    private void send(SseEmitter sseEmitter, String emitterId, Object data) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(data, MediaType.APPLICATION_JSON));
        } catch(IOException exception) {
            emitterRepository.deleteById(emitterId);
            sseEmitter.completeWithError(exception);
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
                .forEach(entry -> emitEventToClient(emitter, entry.getKey(), entry.getValue()));
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

    /* Error handler */
    private void handleEmitterCompletion(String emitterId) {
        log.info("Emitter completed: {}", emitterId);
        emitterRepository.deleteById(emitterId);
    }

    private void handleEmitterTimeout(String emitterId) {
        log.warn("Emitter timeout: {}", emitterId);
        emitterRepository.deleteById(emitterId);
    }

    private void handleEmitterError(String emitterId, Throwable e) {
        log.error("Emitter error: {}, exception: {}", emitterId, e.getMessage());
        emitterRepository.deleteById(emitterId);
    }

    private void handleSendError(SseEmitter emitter, String emitterId, Exception exception) {
        log.error("Error sending event to emitter: {}, exception: {}", emitterId, exception.getMessage());
        emitterRepository.deleteById(emitterId);
        emitter.completeWithError(exception);
    }

}

package com.Sucat.domain.notify.service;

import com.Sucat.domain.notify.exception.NotifyException;
import com.Sucat.domain.notify.model.Notify;
import com.Sucat.domain.notify.model.NotifyType;
import com.Sucat.domain.notify.repository.EmitterRepository;
import com.Sucat.domain.notify.repository.NotifyQueryRepository;
import com.Sucat.domain.notify.repository.NotifyRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.global.common.code.ErrorCode;
import com.Sucat.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.Sucat.domain.notify.dto.NotifyDto.FindNotifyResponse;
import static com.Sucat.domain.notify.dto.NotifyDto.ReadNotifyRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotifyService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotifyRepository notifyRepository;
    private final NotifyQueryRepository notifyQueryRepository;
    private final EmitterRepository emitterRepository;
    private final JwtUtil jwtUtil;

    /**
     * [SSE 연결 메서드]
     * 알림 서버 접속 시 요청 회원의 고유 이벤트 id를 key, SseEmitter 인스턴스를 value로
     * 알림 서버 저장소에 추가합니다.
     */
    public SseEmitter subscribe(HttpServletRequest request, String lastEventId) {
        String email = jwtUtil.getEmailFromRequest(request);

        // 매 연결마다 고유 이벤트 id 부여
        String emitterId = makeTimeIncludeId(email);

        // SseEmitter 인스턴스 생성 후 Map에 저장
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        log.info("new emitter added : {}", emitter);
        log.info("lastEventId : {}", lastEventId);

        // 이벤트 전송 시
        emitter.onCompletion(() -> {
            handleEmitterCompletion(emitterId);
        });

        // 이벤트 스트림 연결 끊길 시
        emitter.onTimeout(() -> {
            handleEmitterTimeout(emitterId);
        });

        // 에러가 발생할 시
        emitter.onError((e) -> {
            handleEmitterError(emitterId, e);
        });

        /* 첫 연결 시 503 Service Unavailable 방지용 Dummy event 전송 */
        String eventId = makeTimeIncludeId(email);
        sendNotification(emitter, eventId, "EventStream Created. [userEmail=" + email + "]");

        /* client가 미수신한 event 목록이 존재하는 경우 */
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, email, emitterId, emitter);
        }

        return emitter;
    }

    /* [SSE 통신] specific user에게 알림 전송 */
    @Async
    public void send(User receiver, NotifyType notifyType, String content, String url) {
        Notify notify = notifyRepository.save(createNotify(receiver, notifyType, content, url));

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(receiver.getEmail());
        emitters.forEach(
                (key, emitter) -> {
                    try {
                        log.info("key, notify : {}, {}", key, notify);
                        emitterRepository.saveEventCache(key, notify); // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                        emitEventToClient(emitter, key, notify); // 데이터 전송
                    } catch (Exception e) {
                        handleSendError(emitter, key, e);
                    }
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
    private void emitEventToClient(
            SseEmitter emitter,
            String emitterId,
            Object data) {
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

    /* 알림 목록 */
    public List<FindNotifyResponse> find(HttpServletRequest request) {
        User user = jwtUtil.getUserFromRequest(request);
        Long userId = user.getId();
        List<Notify> notifyList = notifyQueryRepository.findByUserId(userId, LocalDateTime.now().minusDays(31));

        return notifyList.stream().map(
                FindNotifyResponse::of
        ).toList();
    }

    /* 알림 수정 메서드
    * 사용자가 알림을 읽으면 isRead를 True로 수정
    *  */
    @Transactional
    public void read(List<ReadNotifyRequest> readNotifyRequestList) {
        for (ReadNotifyRequest readNotifyRequest : readNotifyRequestList) {
            Notify notify = getNotifyById(readNotifyRequest.notifyId());
            notify.updateIsRead();
        }
    }

    public Notify getNotifyById(Long id) {
        return notifyRepository.findById(id)
                .orElseThrow(() -> new NotifyException(ErrorCode.NOTIFY_NOT_FOUND));
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

package com.Sucat.domain.notify.aop;

import com.Sucat.domain.notify.aop.proxy.NotifyInfo;
import com.Sucat.domain.notify.dto.NotifyMessage;
import com.Sucat.domain.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class NotifyAspect {
    private final NotifyService notifyService;

    @Pointcut("@annotation(com.Sucat.domain.notify.annotation.NeedNotify)") // NeedNotify 어노테이션이 적용된 메서드들을 대상을 AOP를 적용
    public void annotationPointcut() {

    }

    @Async // 해당 메서드를 비동기적으로 실행하도록 지정
    @AfterReturning(pointcut = "annotationPointcut()", returning = "result") // 해당 포인트컷이 정상적으로 실행되었을 시 수행
    public void checkValue(JoinPoint joinPoint, Object result) throws Throwable {
        NotifyInfo notifyProxy = (NotifyInfo) result;
        notifyService.send(
                notifyProxy.getReceiver(),
                notifyProxy.getNotifyType(),
                NotifyMessage.NEW_REQUEST.getMessage(),
                "/api/v1/" + (notifyProxy.getGoUrlId()) // 주소 수정
        );
        log.info("result = {}", result);
    }
}

package com.Sucat.domain.notify.repository;

import com.Sucat.domain.notify.model.Notify;

import java.time.LocalDateTime;
import java.util.List;

public interface NotifyQueryRepository {
    List<Notify> findByUserId(Long userId, LocalDateTime date);
}

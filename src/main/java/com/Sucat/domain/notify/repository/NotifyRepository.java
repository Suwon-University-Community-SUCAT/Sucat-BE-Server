package com.Sucat.domain.notify.repository;

import com.Sucat.domain.notify.model.Notify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifyRepository extends JpaRepository<Notify, Long> {
}

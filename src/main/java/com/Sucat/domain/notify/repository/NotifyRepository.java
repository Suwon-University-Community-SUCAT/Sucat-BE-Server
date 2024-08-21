package com.Sucat.domain.notify.repository;

import com.Sucat.domain.notify.model.Notify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, Long> {
    @Modifying
    @Query("""
            DELETE FROM Notification n
            WHERE n.createdAt< :date
            """)
    void deleteByCreatedAt(@Param("date") final LocalDateTime date);
}

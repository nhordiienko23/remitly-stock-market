package com.remitly.remitlystockmarket.repository;

import com.remitly.remitlystockmarket.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Custom method to return logs in order of occurrence
    List<AuditLog> findAllByOrderByTimestampAsc();
}
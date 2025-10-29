package com.petstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petstore.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
}

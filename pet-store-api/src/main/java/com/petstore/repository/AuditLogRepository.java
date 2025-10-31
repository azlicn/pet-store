package com.petstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.petstore.model.AuditLog;

/**
 * Repository for managing audit log entities in the database
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

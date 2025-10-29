package com.petstore.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityType;
    private Long entityId;
    private String action;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public AuditLog(String entityType, Long entityId, String action, String oldValue, String newValue,
            LocalDateTime createdAt) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getEntityType() {
        return entityType;
    }


    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }


    public Long getEntityId() {
        return entityId;
    }


    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }


    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }


    public String getOldValue() {
        return oldValue;
    }


    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }


    public String getNewValue() {
        return newValue;
    }


    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}

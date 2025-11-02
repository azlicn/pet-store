package com.petstore.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity class representing an audit trail log entry for tracking changes to
 * system entities.
 */
@Entity
@Table(name = "audit_logs")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    @NotNull(message = "Entity ID is required")
    private Long entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnore
    private User user;

    private String action;
    private String oldValue;
    private String newValue;

    // Auditing fields
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    /**
     * Default constructor.
     */
    public AuditLog() {
    }

    /**
     * Constructs a new audit log entry with all required information.
     * 
     * @param entityType the type of entity being audited (e.g., "Order", "Pet")
     * @param entityId   the unique identifier of the entity being audited
     * @param user       the user who performed the action (can be null for system
     *                   actions)
     * @param action     the action performed (e.g., "CREATE_ORDER",
     *                   "CHECKOUT_ORDER", "CANCEL_ORDER", "UPDATE_DELIVERY_STATUS")
     * @param oldValue   the previous value before the change (JSON format or string
     *                   representation)
     * @param newValue   the new value after the change (JSON format or string
     *                   representation)
     */
    public AuditLog(String entityType, Long entityId, User user, String action, String oldValue, String newValue) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.user = user;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Gets the unique identifier of this audit log entry.
     * 
     * @return the audit log ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this audit log entry.
     * 
     * @param id the audit log ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the type of entity that was audited.
     * 
     * @return the entity type name
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Sets the type of entity that was audited.
     * 
     * @param entityType the entity type name to set (cannot be blank)
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * Gets the unique identifier of the entity that was audited.
     * 
     * @return the entity ID
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * Sets the unique identifier of the entity that was audited.
     * 
     * @param entityId the entity ID to set (cannot be null)
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets the user who performed the audited action.
     * 
     * @return the user entity, or null if the action was system-initiated
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who performed the audited action.
     * 
     * @param user the user entity to set (nullable for system actions)
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the action that was performed on the entity.
     * 
     * @return the action performed
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action that was performed on the entity.
     * 
     * @param action the action to set (e.g., "CREATE_ORDER", "UPDATE_STATUS")
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets the previous value before the change was made.
     * 
     * @return the old value, or null if not applicable
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * Sets the previous value before the change was made.
     * 
     * @param oldValue the old value to set (nullable)
     */
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    /**
     * Gets the new value after the change was made.
     * 
     * @return the new value, or null if not applicable
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * Sets the new value after the change was made.
     * 
     * @param newValue the new value to set (nullable)
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    /**
     * Gets the creation timestamp of this audit log entry.
     *
     * @return when this audit log entry was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of this audit log entry.
     *
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp of this audit log entry.
     *
     * @return when this audit log entry was last updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp of this audit log entry.
     *
     * @param updatedAt the timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the ID of the user who created this audit log entry.
     *
     * @return ID of the creator, or null if created by system
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the ID of the user who created this audit log entry.
     *
     * @param createdBy the creator's ID to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the ID of the user who last modified this audit log
     *
     * @return ID of the last modifier
     */
    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * Sets the ID of the user who last modified this audit log
     *
     * @param lastModifiedBy the last modifier's ID to set
     */
    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

}

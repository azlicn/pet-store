package com.petstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "discounts")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    @NotBlank(message = "Discount code cannot be blank")
    @Size(max = 20, message = "Discount code cannot exceed 20 characters")
    private String code;

    @Column(nullable = false)
    @NotNull(message = "Discount percentage cannot be null")
    private BigDecimal percentage;

    @Column(nullable = false)
    @NotNull(message = "Valid from date cannot be null")
    private LocalDateTime validFrom;

    @Column(nullable = false)
    @NotNull(message = "Valid to date cannot be null")
    private LocalDateTime validTo;

    @Column(length = 200)
    @Size(max = 200, message = "Discount description cannot exceed 200 characters")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Active status cannot be null")
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Discount [id=" + id + ", code=" + code + ", percentage=" + percentage + ", validFrom=" + validFrom
                + ", validTo=" + validTo + ", description=" + description + ", active=" + active + ", createdAt="
                + createdAt + ", updatedAt=" + updatedAt + "]";
    }

    
}

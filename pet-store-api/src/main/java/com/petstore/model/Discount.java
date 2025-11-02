package com.petstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *  Entity class representing a discount offer in the system, including code, percentage, validity period, and status.
 */
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

    /**
     * Gets the unique identifier of the discount.
     * 
     * @return the discount ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the discount.
     * 
     * @param id the discount ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the discount code.
     * 
     * @return the discount code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the discount code.
     * 
     * @param code the discount code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the discount percentage.
     * 
     * @return the discount percentage
     */
    public BigDecimal getPercentage() {
        return percentage;
    }

    /**
     * Sets the discount percentage.
     * 
     * @param percentage the discount percentage
     */
    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    /**
     * Gets the start date and time when the discount is valid.
     * 
     * @return the valid from date
     */
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the start date and time when the discount is valid.
     * 
     * @param validFrom the valid from date
     */
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Gets the end date and time when the discount is valid.
     * 
     * @return the valid to date
     */
    public LocalDateTime getValidTo() {
        return validTo;
    }

    /**
     * Sets the end date and time when the discount is valid.
     * 
     * @param validTo the valid to date
     */
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    /**
     * Gets the description of the discount.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the discount.
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Checks if the discount is active.
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of the discount.
     * 
     * @param active true to activate, false to deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the creation timestamp of the discount.
     * 
     * @return the created at timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the last updated timestamp of the discount.
     * 
     * @return the updated at timestamp
     */
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

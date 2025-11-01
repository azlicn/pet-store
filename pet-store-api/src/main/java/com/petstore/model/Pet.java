package com.petstore.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.petstore.enums.PetStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a pet in the store
 * Contains details about the pet, its category, owner, and audit information
 */
@Entity
@Table(name = "pets")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pet name is required")
    @Column(nullable = false)
    @Size(max = 50, message = "Pet name cannot exceed 50 characters")
    private String name;

    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetStatus status = PetStatus.AVAILABLE;

    // Owner of the pet (null = store inventory, not null = customer purchase)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ElementCollection
    @CollectionTable(name = "pet_photos", joinColumns = @JoinColumn(name = "pet_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls;

    @ElementCollection
    @CollectionTable(name = "pet_tags", joinColumns = @JoinColumn(name = "pet_id"))
    @Column(name = "tag")
    private List<String> tags;

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
     * Default constructor
     */
    public Pet() {
    }

    /**
     * Creates a new pet with basic details
     *
     * @param name the name of the pet
     * @param category the category the pet belongs to
     * @param price the price of the pet
     */
    public Pet(String name, Category category, BigDecimal price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    /**
     * Creates a new pet with owner details
     *
     * @param name the name of the pet
     * @param category the category the pet belongs to
     * @param price the price of the pet
     * @param owner the owner of the pet
     */
    public Pet(String name, Category category, BigDecimal price, User owner) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.owner = owner;
    }

    /**
     * Gets the pet ID
     *
     * @return the pet ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the pet ID
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the pet name
     *
     * @return the pet name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the pet name
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the pet description
     *
     * @return the pet description
     */
    public String getDescription() {
        return description;
    }

     /**
     * Sets the pet description
     *
     * @param name the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the pet category
     *
     * @return the category the pet belongs to
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the pet category
     *
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets the pet price
     *
     * @return the price of the pet
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the pet price
     *
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets the pet status
     *
     * @return the current status of the pet
     */
    public PetStatus getStatus() {
        return status;
    }

    /**
     * Sets the pet status
     *
     * @param status the status to set
     */
    public void setStatus(PetStatus status) {
        this.status = status;
    }

    /**
     * Gets the pet owner
     *
     * @return the owner of the pet, or null if in store inventory
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets the pet owner
     *
     * @param owner the owner to set, or null for store inventory
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Gets the pet photo URLs
     *
     * @return list of photo URLs for the pet
     */
    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    /**
     * Sets the pet photo URLs
     *
     * @param photoUrls the list of photo URLs to set
     */
    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    /**
     * Gets the pet tags
     *
     * @return list of tags associated with the pet
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets the pet tags
     *
     * @param tags the list of tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Gets the creation timestamp
     *
     * @return when this pet was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp
     *
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp
     *
     * @return when this pet was last updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp
     *
     * @param updatedAt the timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the ID of the user who created this pet
     *
     * @return ID of the creator
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the ID of the user who created this pet
     *
     * @param createdBy the creator's ID to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the ID of the user who last modified this pet
     *
     * @return ID of the last modifier
     */
    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * Sets the ID of the user who last modified this pet
     *
     * @param lastModifiedBy the modifier's ID to set
     */
    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    
}
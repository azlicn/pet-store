package com.petstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Entity representing a pet category in the store
 */
@Entity
@Table(name = "categories")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    @Column(nullable = false, unique = true)
    @Size(max = 30, message = "Category name cannot exceed 30 characters")
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Pet> pets;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Sets creation and update timestamps before persisting
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the last modified timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Default constructor
     */
    public Category() {
    }

    /**
     * Creates a new category with the given name
     *
     * @param name the name of the category
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * Gets the category ID
     *
     * @return the category ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the category ID
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the category name
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the category name
     *
     * @param name the name to set (will be trimmed)
     */
    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    /**
     * Gets the pets in this category
     *
     * @return set of pets in this category
     */
    public Set<Pet> getPets() {
        return pets;
    }

    /**
     * Sets the pets in this category
     *
     * @param pets the pets to set
     */
    public void setPets(Set<Pet> pets) {
        this.pets = pets;
    }

    /**
     * Gets the creation timestamp
     *
     * @return when this category was created
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
     * @return when this category was last updated
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
}
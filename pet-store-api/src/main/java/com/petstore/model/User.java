package com.petstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a user in the pet store system
 * Contains user authentication and profile information
 */
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Address> addresses = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Sets initial values before persisting
     * Normalizes email and sets creation timestamps
     */
    @PrePersist
    protected void onCreate() {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates values before saving changes
     */
    @PreUpdate
    protected void onUpdate() {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        updatedAt = LocalDateTime.now();
    }

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Creates a new user with the given details
     *
     * @param email user's email address
     * @param password user's password
     * @param firstName user's first name
     * @param lastName user's last name
     */
    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles.add(Role.USER); // Default role
    }

    public User(Long userId) {
        this.id = userId;
    }

    /**
     * Gets the user ID
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user ID
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user's email address
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address
     * Email will be trimmed and converted to lowercase
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    /**
     * Gets the user's password (hashed)
     *
     * @return the hashed password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password
     *
     * @param password the password to set (will be hashed before storage)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's first name
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's last name
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's roles
     *
     * @return set of roles assigned to the user
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * Sets the user's roles
     *
     * @param roles the roles to assign to the user
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * Gets the user's addresses
     *
     * @return list of addresses associated with the user
     */
    public List<Address> getAddresses() {
        return addresses;
    }

    /**
     * Sets the user's addresses
     *
     * @param addresses the addresses to assign to the user
     */
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * Gets the creation timestamp
     *
     * @return when this user was created
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
     * @return when this user was last updated
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
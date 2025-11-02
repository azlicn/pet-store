package com.petstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entity class representing a user's delivery or billing address.
 */
@Entity
@Table(name = "addresses")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    @NotBlank(message = "Full name is mandatory")
    private String fullName;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Phone number is mandatory")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Street is mandatory")
    @Size(max = 255, message = "Street cannot exceed 255 characters")
    private String street;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "City is mandatory")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "State is mandatory")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Postal code is mandatory")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Country is mandatory")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    private boolean isDefault = false;

    /**
     * Gets the unique identifier of this address.
     * 
     * @return the address ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this address.
     * 
     * @param id the address ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user who owns this address.
     * 
     * @return the user entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who owns this address.
     * 
     * @param user the user entity to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the full name of the recipient at this address.
     * 
     * @return the recipient's full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the recipient at this address.
     * 
     * @param fullName the recipient's full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the contact phone number for this address.
     * 
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the contact phone number for this address.
     * 
     * @param phoneNumber the phone number to set (max 20 characters)
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the street address including house/building number.
     * 
     * @return the street address
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the street address including house/building number.
     * 
     * @param street the street address to set (max 255 characters)
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the city name.
     * 
     * @return the city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city name.
     * 
     * @param city the city name to set (max 100 characters)
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state or province name.
     * 
     * @return the state/province name
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state or province name.
     * 
     * @param state the state/province name to set (max 100 characters)
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the postal or ZIP code.
     * 
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postal or ZIP code.
     * 
     * @param postalCode the postal code to set (max 20 characters)
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the country name.
     * 
     * @return the country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country name.
     * 
     * @param country the country name to set (max 100 characters)
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Checks if this is the user's default address.
     * <p>
     * The default address is automatically selected during checkout
     * and used as the primary delivery address for orders.
     * </p>
     * 
     * @return true if this is the default address, false otherwise
     */
    @JsonProperty("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Sets whether this is the user's default address.
     * <p>
     * Only one address per user should be marked as default.
     * Setting a new default address should unset the previous default.
     * </p>
     * 
     * @param isDefault true to set as default address, false otherwise
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Generates a formatted multi-line string representation of the complete
     * address.
     * <p>
     * The format is:
     * 
     * <pre>
     * Street
     * City,
     * State PostalCode
     * Country
     * </pre>
     * </p>
     * 
     * @return the formatted address string suitable for display or printing
     */
    public String getFullAddress() {
        return String.format(
                "%s\n%s, \n%s %s\n%s",
                street, city, state, postalCode, country);
    }

}

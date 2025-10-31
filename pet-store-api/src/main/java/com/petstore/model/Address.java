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
import jakarta.validation.constraints.NotNull;

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
    @NotBlank(
            message = "Full name is mandatory")
    private String fullName;

    @Column(nullable = false)
    @NotBlank(
            message = "Phone number is mandatory")
    private String phoneNumber;

    @Column(nullable = false)
    @NotBlank(
            message = "Street is mandatory")
    private String street;

    @Column(nullable = false)
    @NotBlank(
            message = "City is mandatory")
    private String city;

    @Column(nullable = false)
    @NotBlank(
            message = "State is mandatory")
    private String state;

    @Column(nullable = false)
    @NotBlank(
            message = "Postal code is mandatory")
    private String postalCode;

    @Column(nullable = false)
    @NotBlank(
            message = "Country is mandatory")
    private String country;

    private boolean isDefault = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("isDefault")
    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getFullAddress() {
        return String.format(
                "%s\n%s, \n%s %s\n%s",
                street, city, state, postalCode, country);
    }

}

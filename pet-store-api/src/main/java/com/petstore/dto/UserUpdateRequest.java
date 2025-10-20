package com.petstore.dto;

import com.petstore.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Data Transfer Object for user update requests
 */
public class UserUpdateRequest {
    
    @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
    private String firstName;
    
    @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
    private String lastName;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @Size(min = 6, max = 100, message = "Password should be between 6 and 100 characters")
    private String password;
    
    private Set<Role> roles;

    public UserUpdateRequest() {}

    public UserUpdateRequest(String firstName, String lastName, String email, String password, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public String getFirstName() { 
        return firstName; 
    }
    
    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    public String getLastName() { 
        return lastName; 
    }
    
    public void setLastName(String lastName) { 
        this.lastName = lastName; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }

    public Set<Role> getRoles() { 
        return roles; 
    }
    
    public void setRoles(Set<Role> roles) { 
        this.roles = roles; 
    }

    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", roles=" + roles +
                '}';
    }
}
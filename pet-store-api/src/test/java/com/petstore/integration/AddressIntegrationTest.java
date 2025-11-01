package com.petstore.integration;

import com.petstore.model.Role;
import com.petstore.model.Address;
import com.petstore.model.User;
import com.petstore.repository.AddressRepository;
import com.petstore.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Address API endpoints.
 * Tests address CRUD operations, authorization, and validation.
 */
public class AddressIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User otherUser;
    private String userToken;
    private String otherUserToken;
    private Address testAddress1;
    private Address testAddress2;

    @BeforeEach
    public void setUp() {
        // Clean up
        addressRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setEmail("addressuser@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("Address");
        testUser.setLastName("User");
        testUser.setRoles(Set.of(Role.USER));
        testUser = userRepository.save(testUser);

        // Create another user
        otherUser = new User();
        otherUser.setEmail("otheruser@example.com");
        otherUser.setPassword(passwordEncoder.encode("password123"));
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");
        otherUser.setRoles(Set.of(Role.USER));
        otherUser = userRepository.save(otherUser);

        // Generate tokens
        userToken = generateToken(testUser);
        otherUserToken = generateToken(otherUser);

        // Create test addresses
        testAddress1 = new Address();
        testAddress1.setFullName("John Doe");
        testAddress1.setPhoneNumber("555-1234");
        testAddress1.setStreet("123 Main St");
        testAddress1.setCity("Springfield");
        testAddress1.setState("IL");
        testAddress1.setPostalCode("62701");
        testAddress1.setCountry("USA");
        testAddress1.setDefault(true);
        testAddress1.setUser(testUser);
        testAddress1 = addressRepository.save(testAddress1);

        testAddress2 = new Address();
        testAddress2.setFullName("Jane Doe");
        testAddress2.setPhoneNumber("555-5678");
        testAddress2.setStreet("456 Oak Ave");
        testAddress2.setCity("Chicago");
        testAddress2.setState("IL");
        testAddress2.setPostalCode("60601");
        testAddress2.setCountry("USA");
        testAddress2.setDefault(false);
        testAddress2.setUser(testUser);
        testAddress2 = addressRepository.save(testAddress2);
    }

    // ==================== Get Addresses Tests ====================

    @Test
    @DisplayName("Should get all addresses for authenticated user")
    public void testGetAddresses_Success() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/users/addresses")
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$[0].isDefault").value(true))
                .andExpect(jsonPath("$[1].fullName").value("Jane Doe"))
                .andExpect(jsonPath("$[1].isDefault").value(false));
    }

    @Test
    @DisplayName("Should return empty list when user has no addresses")
    public void testGetAddresses_EmptyList() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/users/addresses")
                .header("Authorization", createAuthorizationHeader(otherUserToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Should fail to get addresses without authentication")
    public void testGetAddresses_Unauthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/users/addresses"));

        result.andExpect(status().isForbidden());
    }

    // ==================== Create Address Tests ====================

    @Test
    @DisplayName("Should create new address successfully")
    public void testCreateAddress_Success() throws Exception {
        Address newAddress = new Address();
        newAddress.setFullName("Bob Smith");
        newAddress.setPhoneNumber("555-9999");
        newAddress.setStreet("789 Elm St");
        newAddress.setCity("Boston");
        newAddress.setState("MA");
        newAddress.setPostalCode("02101");
        newAddress.setCountry("USA");

        ResultActions result = mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddress))
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullName").value("Bob Smith"))
                .andExpect(jsonPath("$.phoneNumber").value("555-9999"))
                .andExpect(jsonPath("$.street").value("789 Elm St"))
                .andExpect(jsonPath("$.city").value("Boston"))
                .andExpect(jsonPath("$.state").value("MA"))
                .andExpect(jsonPath("$.postalCode").value("02101"))
                .andExpect(jsonPath("$.country").value("USA"));
    }

    @Test
    @DisplayName("Should set first address as default automatically")
    public void testCreateAddress_FirstAddressDefault() throws Exception {
        // Use other user who has no addresses
        Address newAddress = new Address();
        newAddress.setFullName("First Address");
        newAddress.setPhoneNumber("555-0001");
        newAddress.setStreet("1 First St");
        newAddress.setCity("Austin");
        newAddress.setState("TX");
        newAddress.setPostalCode("78701");
        newAddress.setCountry("USA");

        ResultActions result = mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddress))
                .header("Authorization", createAuthorizationHeader(otherUserToken)));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    @DisplayName("Should fail to create address with missing required fields")
    public void testCreateAddress_ValidationError_MissingFields() throws Exception {
        Address invalidAddress = new Address();
        invalidAddress.setFullName("Test User");
        // Missing other required fields

        ResultActions result = mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAddress))
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should fail to create address with blank fields")
    public void testCreateAddress_ValidationError_BlankFields() throws Exception {
        Address invalidAddress = new Address();
        invalidAddress.setFullName("  ");
        invalidAddress.setPhoneNumber("555-0000");
        invalidAddress.setStreet("   ");
        invalidAddress.setCity("Test");
        invalidAddress.setState("TX");
        invalidAddress.setPostalCode("12345");
        invalidAddress.setCountry("USA");

        ResultActions result = mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidAddress))
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("mandatory")));
    }

    @Test
    @DisplayName("Should fail to create address without authentication")
    public void testCreateAddress_Unauthorized() throws Exception {
        Address newAddress = new Address();
        newAddress.setFullName("Test");
        newAddress.setPhoneNumber("555-0000");
        newAddress.setStreet("123 Test St");
        newAddress.setCity("Test City");
        newAddress.setState("TX");
        newAddress.setPostalCode("12345");
        newAddress.setCountry("USA");

        ResultActions result = mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddress)));

        result.andExpect(status().isForbidden());
    }

    // ==================== Update Address Tests ====================

    @Test
    @DisplayName("Should update address successfully")
    public void testUpdateAddress_Success() throws Exception {
        Address updateData = new Address();
        updateData.setFullName("John Updated");
        updateData.setPhoneNumber("555-XXXX");
        updateData.setStreet("999 Updated St");
        updateData.setCity("New City");
        updateData.setState("CA");
        updateData.setPostalCode("90001");
        updateData.setCountry("USA");
        updateData.setDefault(false);

        ResultActions result = mockMvc.perform(put("/api/users/addresses/" + testAddress1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData))
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testAddress1.getId()))
                .andExpect(jsonPath("$.fullName").value("John Updated"))
                .andExpect(jsonPath("$.phoneNumber").value("555-XXXX"))
                .andExpect(jsonPath("$.street").value("999 Updated St"))
                .andExpect(jsonPath("$.city").value("New City"))
                .andExpect(jsonPath("$.state").value("CA"))
                .andExpect(jsonPath("$.postalCode").value("90001"))
                .andExpect(jsonPath("$.isDefault").value(false));
    }

    @Test
    @DisplayName("Should fail to update non-existent address")
    public void testUpdateAddress_NotFound() throws Exception {
        Address updateData = new Address();
        updateData.setFullName("Test");
        updateData.setPhoneNumber("555-0000");
        updateData.setStreet("123 Test");
        updateData.setCity("Test");
        updateData.setState("TX");
        updateData.setPostalCode("12345");
        updateData.setCountry("USA");

        ResultActions result = mockMvc.perform(put("/api/users/addresses/99999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData))
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Address not found")));
    }

    @Test
    @DisplayName("Should fail to update address with validation errors")
    public void testUpdateAddress_ValidationError() throws Exception {
        Address updateData = new Address();
        updateData.setFullName("");
        updateData.setPhoneNumber("555-0000");
        updateData.setStreet("123 Test");
        updateData.setCity("Test");
        updateData.setState("TX");
        updateData.setPostalCode("12345");
        updateData.setCountry("USA");

        ResultActions result = mockMvc.perform(put("/api/users/addresses/" + testAddress1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData))
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should fail to update address without authentication")
    public void testUpdateAddress_Unauthorized() throws Exception {
        Address updateData = new Address();
        updateData.setFullName("Test");
        updateData.setPhoneNumber("555-0000");
        updateData.setStreet("123 Test");
        updateData.setCity("Test");
        updateData.setState("TX");
        updateData.setPostalCode("12345");
        updateData.setCountry("USA");

        ResultActions result = mockMvc.perform(put("/api/users/addresses/" + testAddress1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)));

        result.andExpect(status().isForbidden());
    }

    // ==================== Delete Address Tests ====================

    @Test
    @DisplayName("Should delete address successfully")
    public void testDeleteAddress_Success() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/users/addresses/" + testAddress2.getId())
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isNoContent());

        // Verify address is deleted
        mockMvc.perform(get("/api/users/addresses")
                .header("Authorization", createAuthorizationHeader(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(testAddress1.getId()));
    }

    @Test
    @DisplayName("Should fail to delete non-existent address")
    public void testDeleteAddress_NotFound() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/users/addresses/99999")
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Address not found")));
    }

    @Test
    @DisplayName("Should fail to delete address without authentication")
    public void testDeleteAddress_Unauthorized() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/users/addresses/" + testAddress1.getId()));

        result.andExpect(status().isForbidden());
    }

    // ==================== Address Ownership & Security Tests ====================

    @Test
    @DisplayName("Should only return addresses belonging to authenticated user")
    public void testGetAddresses_OnlyOwnAddresses() throws Exception {
        // Create address for other user
        Address otherUserAddress = new Address();
        otherUserAddress.setFullName("Other User Address");
        otherUserAddress.setPhoneNumber("555-7777");
        otherUserAddress.setStreet("777 Other St");
        otherUserAddress.setCity("Seattle");
        otherUserAddress.setState("WA");
        otherUserAddress.setPostalCode("98101");
        otherUserAddress.setCountry("USA");
        otherUserAddress.setUser(otherUser);
        addressRepository.save(otherUserAddress);

        // Test user should only see their own addresses
        ResultActions result = mockMvc.perform(get("/api/users/addresses")
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].fullName").value(not(hasItem("Other User Address"))));
    }
}

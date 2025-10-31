package com.petstore.controller;

import com.petstore.model.Address;
import com.petstore.model.User;
import com.petstore.service.AddressService;
import com.petstore.service.UserService;
import com.petstore.config.TestSecurityConfig;
import com.petstore.exception.GlobalExceptionHandler;
import com.petstore.security.JwtTokenProvider;
import com.petstore.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * WebMvcTest for AddressController.
 * <p>
 * This test class covers all controller endpoints for address management,
 * including CRUD operations,
 * edge cases, negative scenarios, and security/authorization checks. It uses
 * MockMvc for HTTP request simulation
 * and mocks the service layer to isolate controller logic. Security filters are
 * enabled for realistic access control testing.
 */
@WebMvcTest(AddressController.class)
@Import({ GlobalExceptionHandler.class, TestSecurityConfig.class })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Address Controller WebMvcTest")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test: GET /api/users/addresses
     * Verifies that all addresses are returned successfully for an authenticated
     * user.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /api/users/addresses - should return all addresses")
    void shouldReturnAllAddresses() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        Address addr = new Address();
        addr.setId(10L);
        addr.setStreet("123 Main St");
        addr.setCity("City");
        addr.setState("State");
        addr.setPostalCode("12345");
        addr.setCountry("Country");
        when(addressService.getUserAddresses(eq(1L))).thenReturn(List.of(addr));
        mockMvc.perform(get("/api/users/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].street").value("123 Main St"));
    }

    /**
     * Test: GET /api/users/addresses (user not found)
     * Verifies that a 400 Bad Request is returned if the user is not found.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/users/addresses - should return 400 if user not found")
    void shouldReturn400IfUserNotFound() throws Exception {

        when(userService.getUserByEmail(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/users/addresses"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST /api/users/addresses
     * Verifies that a new address is created successfully for an authenticated
     * user.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/users/addresses - should create address successfully")
    void shouldCreateAddressSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        Address newAddr = new Address();
        newAddr.setFullName("Abraham");
        newAddr.setStreet("456 Elm St");
        newAddr.setCity("City");
        newAddr.setState("State");
        newAddr.setPostalCode("67890");
        newAddr.setCountry("Country");
        newAddr.setPhoneNumber("112233445566");
        Address savedAddr = new Address();
        savedAddr.setId(20L);
        savedAddr.setFullName("Abraham");
        savedAddr.setStreet("456 Elm St");
        savedAddr.setCity("City");
        savedAddr.setState("State");
        savedAddr.setPostalCode("67890");
        savedAddr.setCountry("Country");
        savedAddr.setPhoneNumber("112233445566");
        when(addressService.createAddress(eq(1L), any(Address.class))).thenReturn(savedAddr);
        mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddr)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.street").value("456 Elm St"));
    }

    /**
     * Test: POST /api/users/addresses (user not found)
     * Verifies that a 400 Bad Request is returned if the user is not found.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/users/addresses - should return 400 if user not found")
    void shouldReturn400OnCreateIfUserNotFound() throws Exception {

        when(userService.getUserByEmail(any())).thenReturn(Optional.empty());
        Address newAddr = new Address();
        newAddr.setStreet("789 Oak St");
        newAddr.setCity("City");
        newAddr.setState("State");
        newAddr.setPostalCode("54321");
        newAddr.setCountry("Country");
        mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddr)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: PUT /api/users/addresses/{id}
     * Verifies that an existing address is updated successfully for an
     * authenticated user.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("PUT /api/users/addresses/{id} - should update address successfully")
    void shouldUpdateAddressSuccessfully() throws Exception {

        Address updateDetails = new Address();
        updateDetails.setFullName("Updated Name");
        updateDetails.setPhoneNumber("112233445566");
        updateDetails.setStreet("Updated Address");
        updateDetails.setCity("City");
        updateDetails.setState("State");
        updateDetails.setPostalCode("11111");
        updateDetails.setCountry("Country");
        Address updatedAddr = new Address();
        updatedAddr.setId(30L);
        updatedAddr.setFullName("Updated Name");
        updatedAddr.setPhoneNumber("112233445566");
        updatedAddr.setStreet("Updated Address");
        updatedAddr.setCity("City");
        updatedAddr.setState("State");
        updatedAddr.setPostalCode("11111");
        updatedAddr.setCountry("Country");
        when(addressService.updateAddress(eq(30L), any(Address.class))).thenReturn(updatedAddr);
        mockMvc.perform(put("/api/users/addresses/30")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.street").value("Updated Address"));
    }

    /**
     * Test: DELETE /api/users/addresses/{id}
     * Verifies that an address is deleted successfully for an authenticated user.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/users/addresses/{id} - should delete address successfully")
    void shouldDeleteAddressSuccessfully() throws Exception {

        mockMvc.perform(delete("/api/users/addresses/40"))
                .andExpect(status().isNoContent());
    }

    /**
     * Test: POST /api/users/addresses (empty street)
     * Verifies that a 400 Bad Request is returned when the street is empty.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/users/addresses - should return 400 for empty street")
    void shouldReturn400ForEmptyStreet() throws Exception {

        Address newAddr = new Address();
        newAddr.setStreet("");
        newAddr.setCity("City");
        newAddr.setState("State");
        newAddr.setPostalCode("12345");
        newAddr.setCountry("Country");
        mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddr)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST /api/users/addresses (null body)
     * Verifies that a 400 Bad Request is returned when the request body is empty.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/users/addresses - should return 400 for null body")
    void shouldReturn400ForNullBody() throws Exception {

        mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(status().isBadRequest());
    }

    /**
     * Test: GET /api/users/addresses (unauthorized)
     * Verifies that a 403 Forbidden is returned for a user without USER role.
     */
    @Test
    @DisplayName("GET /api/users/addresses - should return 403 for unauthorized user")
    void shouldReturn403ForUnauthorizedUserOnGet() throws Exception {

        mockMvc.perform(get("/api/users/addresses"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: POST /api/users/addresses (unauthorized)
     * Verifies that a 403 Forbidden is returned for a user without USER role when
     * creating an address.
     */
    @Test
    @DisplayName("POST /api/users/addresses - should return 403 for unauthorized user")
    void shouldReturn403ForUnauthorizedUserOnCreate() throws Exception {

        Address newAddr = new Address();
        newAddr.setFullName("John Doe");
        newAddr.setPhoneNumber("1234567890");
        newAddr.setStreet("Fish St");
        newAddr.setCity("City");
        newAddr.setState("State");
        newAddr.setPostalCode("22222");
        newAddr.setCountry("Country");
        mockMvc.perform(post("/api/users/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAddr)))
                .andExpect(status().isForbidden());
    }
}

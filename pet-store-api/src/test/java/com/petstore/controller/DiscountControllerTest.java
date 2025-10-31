package com.petstore.controller;

import com.petstore.model.Discount;
import com.petstore.service.DiscountService;
import com.petstore.config.TestSecurityConfig;
import com.petstore.exception.DiscountNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * WebMvcTest for DiscountController.
 * <p>
 * This test class covers all controller endpoints for discount management,
 * including CRUD operations,
 * edge cases, negative scenarios, and security/authorization checks. It uses
 * MockMvc for HTTP request simulation
 * and mocks the service layer to isolate controller logic. Security filters are
 * enabled for realistic access control testing.
 */
@WebMvcTest(DiscountController.class)
@Import({ GlobalExceptionHandler.class, TestSecurityConfig.class })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Discount Controller WebMvcTest")
class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscountService discountService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test: GET /api/discounts - should return all discounts for admin
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/discounts - should return all discounts")
    void shouldReturnAllDiscounts() throws Exception {

        Discount d1 = new Discount();
        d1.setId(1L);
        d1.setCode("SAVE10");
        Discount d2 = new Discount();
        d2.setId(2L);
        d2.setCode("SAVE20");
        when(discountService.getAllDiscounts()).thenReturn(List.of(d1, d2));
        mockMvc.perform(get("/api/discounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("SAVE10"));
    }

    /**
     * Test: GET /api/discounts/{id} - should return discount by id
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/discounts/{id} - should return discount by id")
    void shouldReturnDiscountById() throws Exception {

        Discount d = new Discount();
        d.setId(1L);
        d.setCode("SAVE10");
        when(discountService.getDiscountById(1L)).thenReturn(d);
        mockMvc.perform(get("/api/discounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value(1))
                .andExpect(jsonPath(".code").value("SAVE10"));
    }

    /**
     * Test: GET /api/discounts/{id} - not found
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/discounts/{id} - should return DiscountNotFoundException if not found")
    void shouldReturnDiscountIfNotFound() throws Exception {

        when(discountService.getDiscountById(99L)).thenThrow(DiscountNotFoundException.class);
        mockMvc.perform(get("/api/discounts/99"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test: POST /api/discounts - should create discount
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/discounts - should create discount successfully")
    void shouldCreateDiscountSuccessfully() throws Exception {

        Discount newDiscount = new Discount();
        newDiscount.setCode("NEW10");
        newDiscount.setPercentage(BigDecimal.valueOf(10));
        newDiscount.setValidFrom(LocalDateTime.now());
        newDiscount.setValidTo(LocalDateTime.now().plusDays(30));
        newDiscount.setDescription("New discount");
        newDiscount.setActive(true);

        Discount savedDiscount = new Discount();
        savedDiscount.setId(3L);
        savedDiscount.setCode("NEW10");
        savedDiscount.setPercentage(BigDecimal.valueOf(10));
        savedDiscount.setValidFrom(LocalDateTime.now());
        savedDiscount.setValidTo(LocalDateTime.now().plusDays(30));
        savedDiscount.setDescription("New discount");
        savedDiscount.setActive(true);

        when(discountService.saveDiscount(any(Discount.class))).thenReturn(savedDiscount);
        mockMvc.perform(post("/api/discounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDiscount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value(3))
                .andExpect(jsonPath(".code").value("NEW10"));
    }

    /**
     * Test: PUT /api/discounts/{id} - should update discount
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/discounts/{id} - should update discount successfully")
    void shouldUpdateDiscountSuccessfully() throws Exception {

        Discount updateDetails = new Discount();
        updateDetails.setCode("UPD10");
        updateDetails.setPercentage(BigDecimal.valueOf(15));
        updateDetails.setValidFrom(LocalDateTime.now().minusDays(1));
        updateDetails.setValidTo(LocalDateTime.now().plusDays(30));
        updateDetails.setDescription("Updated discount");
        updateDetails.setActive(true);

        Discount updatedDiscount = new Discount();
        updatedDiscount.setId(1L);
        updatedDiscount.setCode("UPD10");
        updatedDiscount.setPercentage(BigDecimal.valueOf(15));
        updatedDiscount.setValidFrom(updateDetails.getValidFrom());
        updatedDiscount.setValidTo(updateDetails.getValidTo());
        updatedDiscount.setDescription("Updated discount");
        updatedDiscount.setActive(true);

        when(discountService.updateDiscount(eq(1L), any(Discount.class))).thenReturn(updatedDiscount);
        mockMvc.perform(put("/api/discounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value(1))
                .andExpect(jsonPath(".code").value("UPD10"));
    }

    /**
     * Test: PUT /api/discounts/{id} - not found
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/discounts/{id} - should return 404 if not found")
    void shouldReturn404OnUpdateIfNotFound() throws Exception {

        Discount updateDetails = new Discount();
        updateDetails.setCode("UPD10");
        updateDetails.setPercentage(BigDecimal.valueOf(15));
        updateDetails.setValidFrom(LocalDateTime.now().minusDays(1));
        updateDetails.setValidTo(LocalDateTime.now().plusDays(30));
        updateDetails.setDescription("Updated discount");
        updateDetails.setActive(true);

        doThrow(new DiscountNotFoundException(99L)).when(discountService).updateDiscount(eq(99L), any(Discount.class));
        mockMvc.perform(put("/api/discounts/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());
    }

    /**
     * Test: DELETE /api/discounts/{id} - should delete discount
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/discounts/{id} - should delete discount successfully")
    void shouldDeleteDiscountSuccessfully() throws Exception {

        doNothing().when(discountService).deleteDiscount(1L);
        mockMvc.perform(delete("/api/discounts/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Test: DELETE /api/discounts/{id} - not found
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/discounts/{id} - should return 404 if not found")
    void shouldReturn404OnDeleteIfNotFound() throws Exception {

        doThrow(new DiscountNotFoundException(99L)).when(discountService).deleteDiscount(99L);
        mockMvc.perform(delete("/api/discounts/99"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test: GET /api/discounts/validate - should validate discount code
     */
    @Test
    @DisplayName("GET /api/discounts/validate - should validate discount code")
    void shouldValidateDiscountCode() throws Exception {

        Discount validDiscount = new Discount();
        validDiscount.setId(1L);
        validDiscount.setCode("SAVE10");
        when(discountService.validateDiscount("SAVE10")).thenReturn(validDiscount);
        mockMvc.perform(get("/api/discounts/validate?code=SAVE10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value(1))
                .andExpect(jsonPath(".code").value("SAVE10"));
    }

    /**
     * Test: GET /api/discounts/active - should return active discounts for user or
     * admin
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/discounts/active - should return active discounts")
    void shouldReturnActiveDiscounts() throws Exception {

        Discount d1 = new Discount();
        d1.setId(1L);
        d1.setCode("PROMO1");
        Discount d2 = new Discount();
        d2.setId(2L);
        d2.setCode("PROMO2");
        when(discountService.getAllActiveDiscounts()).thenReturn(List.of(d1, d2));
        mockMvc.perform(get("/api/discounts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("PROMO1"));
    }

    /**
     * Test: POST /api/discounts - empty code
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/discounts - should return 400 for empty code")
    void shouldReturn400ForEmptyCode() throws Exception {

        Discount newDiscount = new Discount();
        newDiscount.setCode("");
        mockMvc.perform(post("/api/discounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDiscount)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST /api/discounts - null body
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/discounts - should return 400 for null body")
    void shouldReturn400ForNullBody() throws Exception {

        mockMvc.perform(post("/api/discounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")).andExpect(status().isBadRequest());
    }

    /**
     * Test: GET /api/discounts (unauthorized)
     */
    @Test
    @DisplayName("GET /api/discounts - should return 403 for unauthorized user")
    void shouldReturn403ForUnauthorizedUserOnGet() throws Exception {

        mockMvc.perform(get("/api/discounts"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: POST /api/discounts (unauthorized)
     */
    @Test
    @DisplayName("POST /api/discounts - should return 403 for unauthorized user")
    void shouldReturn403ForUnauthorizedUserOnCreate() throws Exception {

        Discount newDiscount = new Discount();
        newDiscount.setCode("NOAUTH");
        newDiscount.setPercentage(BigDecimal.valueOf(10));
        newDiscount.setValidFrom(LocalDateTime.now());
        newDiscount.setValidTo(LocalDateTime.now().plusDays(30));
        newDiscount.setDescription("New discount");
        newDiscount.setActive(true);

        mockMvc.perform(post("/api/discounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDiscount)))
                .andExpect(status().isForbidden());
    }
}

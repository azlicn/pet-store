package com.petstore.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.petstore.enums.Role;
import com.petstore.model.Discount;
import com.petstore.model.User;
import com.petstore.repository.DiscountRepository;

public class DiscountIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private DiscountRepository discountRepository;

    private Discount testDiscount1;
    private Discount testDiscount2;
    private Discount expiredDiscount;

    @BeforeEach
    public void setUp() {
        // Clean up
        discountRepository.deleteAll();
        userRepository.deleteAll();

        // Create test admin user
        User testAdmin = new User();
        testAdmin.setEmail("admin@test.com");
        testAdmin.setPassword(passwordEncoder.encode("password"));
        testAdmin.setFirstName("Admin");
        testAdmin.setLastName("User");
        testAdmin.setRoles(Set.of(Role.ADMIN));
        testAdmin = userRepository.save(testAdmin);
        adminToken = generateToken(testAdmin);

        // Create test regular user
        User testUser = new User();
        testUser.setEmail("user@test.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRoles(Set.of(Role.USER));
        testUser = userRepository.save(testUser);
        userToken = generateToken(testUser);

        // Create test discounts
        testDiscount1 = new Discount();
        testDiscount1.setCode("SAVE10");
        testDiscount1.setPercentage(new BigDecimal("10.00"));
        testDiscount1.setValidFrom(LocalDateTime.now().minusDays(1));
        testDiscount1.setValidTo(LocalDateTime.now().plusDays(30));
        testDiscount1.setDescription("10% off");
        testDiscount1.setActive(true);
        testDiscount1 = discountRepository.save(testDiscount1);

        testDiscount2 = new Discount();
        testDiscount2.setCode("SAVE20");
        testDiscount2.setPercentage(new BigDecimal("20.00"));
        testDiscount2.setValidFrom(LocalDateTime.now().minusDays(1));
        testDiscount2.setValidTo(LocalDateTime.now().plusDays(30));
        testDiscount2.setDescription("20% off");
        testDiscount2.setActive(true);
        testDiscount2 = discountRepository.save(testDiscount2);

        // Create expired discount
        expiredDiscount = new Discount();
        expiredDiscount.setCode("EXPIRED");
        expiredDiscount.setPercentage(new BigDecimal("15.00"));
        expiredDiscount.setValidFrom(LocalDateTime.now().minusDays(30));
        expiredDiscount.setValidTo(LocalDateTime.now().minusDays(1));
        expiredDiscount.setDescription("Expired discount");
        expiredDiscount.setActive(true);
        expiredDiscount = discountRepository.save(expiredDiscount);
    }

    // ==================== Get All Discounts Tests ====================

    @Test
    @DisplayName("Should get all discounts as admin")
    public void testGetAllDiscounts_Success() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts")
                .header("Authorization", createAuthorizationHeader(adminToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].code").value("SAVE10"))
                .andExpect(jsonPath("$[1].code").value("SAVE20"))
                .andExpect(jsonPath("$[2].code").value("EXPIRED"));
    }

    @Test
    @DisplayName("Should fail to get all discounts without admin role")
    public void testGetAllDiscounts_Forbidden() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts")
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should fail to get all discounts without authentication")
    public void testGetAllDiscounts_Unauthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts"));

        result.andExpect(status().isForbidden());
    }

    // ==================== Get Discount By ID Tests ====================

    @Test
    @DisplayName("Should get discount by ID as admin")
    public void testGetDiscountById_Success() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/" + testDiscount1.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDiscount1.getId()))
                .andExpect(jsonPath("$.code").value("SAVE10"))
                .andExpect(jsonPath("$.percentage").value(10.00));
    }

    @Test
    @DisplayName("Should return 404 when discount not found")
    public void testGetDiscountById_NotFound() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/99999")
                .header("Authorization", createAuthorizationHeader(adminToken)));

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should fail to get discount by ID without admin role")
    public void testGetDiscountById_Forbidden() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/" + testDiscount1.getId())
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isForbidden());
    }

    // ==================== Create Discount Tests ====================

    @Test
    @DisplayName("Should create discount as admin")
    public void testCreateDiscount_Success() throws Exception {
        Discount newDiscount = new Discount();
        newDiscount.setCode("NEW30");
        newDiscount.setPercentage(new BigDecimal("30.00"));
        newDiscount.setValidFrom(LocalDateTime.now());
        newDiscount.setValidTo(LocalDateTime.now().plusDays(60));
        newDiscount.setDescription("30% off special");
        newDiscount.setActive(true);

        ResultActions result = mockMvc.perform(post("/api/discounts")
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDiscount)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("NEW30"))
                .andExpect(jsonPath("$.percentage").value(30.00))
                .andExpect(jsonPath("$.description").value("30% off special"));
    }

    @Test
    @DisplayName("Should fail to create discount with blank code")
    public void testCreateDiscount_BlankCode() throws Exception {
        Discount newDiscount = new Discount();
        newDiscount.setCode("");
        newDiscount.setPercentage(new BigDecimal("10.00"));
        newDiscount.setValidFrom(LocalDateTime.now());
        newDiscount.setValidTo(LocalDateTime.now().plusDays(30));
        newDiscount.setActive(true);

        ResultActions result = mockMvc.perform(post("/api/discounts")
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDiscount)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail to create discount with null percentage")
    public void testCreateDiscount_NullPercentage() throws Exception {
        Discount newDiscount = new Discount();
        newDiscount.setCode("TEST");
        newDiscount.setPercentage(null);
        newDiscount.setValidFrom(LocalDateTime.now());
        newDiscount.setValidTo(LocalDateTime.now().plusDays(30));
        newDiscount.setActive(true);

        ResultActions result = mockMvc.perform(post("/api/discounts")
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDiscount)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail to create discount without admin role")
    public void testCreateDiscount_Forbidden() throws Exception {
        Discount newDiscount = new Discount();
        newDiscount.setCode("NEW30");
        newDiscount.setPercentage(new BigDecimal("30.00"));
        newDiscount.setValidFrom(LocalDateTime.now());
        newDiscount.setValidTo(LocalDateTime.now().plusDays(60));
        newDiscount.setActive(true);

        ResultActions result = mockMvc.perform(post("/api/discounts")
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDiscount)));

        result.andExpect(status().isForbidden());
    }

    // ==================== Update Discount Tests ====================

    @Test
    @DisplayName("Should update discount as admin")
    public void testUpdateDiscount_Success() throws Exception {
        Discount updateData = new Discount();
        updateData.setCode("UPDATED10");
        updateData.setPercentage(new BigDecimal("15.00"));
        updateData.setValidFrom(testDiscount1.getValidFrom());
        updateData.setValidTo(testDiscount1.getValidTo());
        updateData.setDescription("Updated discount");
        updateData.setActive(false);

        ResultActions result = mockMvc.perform(put("/api/discounts/" + testDiscount1.getId())
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("UPDATED10"))
                .andExpect(jsonPath("$.percentage").value(15.00))
                .andExpect(jsonPath("$.description").value("Updated discount"))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent discount")
    public void testUpdateDiscount_NotFound() throws Exception {
        Discount updateData = new Discount();
        updateData.setCode("UPDATED");
        updateData.setPercentage(new BigDecimal("15.00"));
        updateData.setValidFrom(LocalDateTime.now());
        updateData.setValidTo(LocalDateTime.now().plusDays(30));
        updateData.setActive(true);

        ResultActions result = mockMvc.perform(put("/api/discounts/99999")
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)));

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should fail to update discount with duplicate code")
    public void testUpdateDiscount_DuplicateCode() throws Exception {
        Discount updateData = new Discount();
        updateData.setCode("SAVE20"); // Duplicate of testDiscount2
        updateData.setPercentage(new BigDecimal("15.00"));
        updateData.setValidFrom(testDiscount1.getValidFrom());
        updateData.setValidTo(testDiscount1.getValidTo());
        updateData.setActive(true);

        ResultActions result = mockMvc.perform(put("/api/discounts/" + testDiscount1.getId())
                .header("Authorization", createAuthorizationHeader(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)));

        result.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should fail to update discount without admin role")
    public void testUpdateDiscount_Forbidden() throws Exception {
        Discount updateData = new Discount();
        updateData.setCode("UPDATED");
        updateData.setPercentage(new BigDecimal("15.00"));
        updateData.setValidFrom(testDiscount1.getValidFrom());
        updateData.setValidTo(testDiscount1.getValidTo());
        updateData.setActive(true);

        ResultActions result = mockMvc.perform(put("/api/discounts/" + testDiscount1.getId())
                .header("Authorization", createAuthorizationHeader(userToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)));

        result.andExpect(status().isForbidden());
    }

    // ==================== Delete Discount Tests ====================

    @Test
    @DisplayName("Should delete discount as admin")
    public void testDeleteDiscount_Success() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/discounts/" + testDiscount1.getId())
                .header("Authorization", createAuthorizationHeader(adminToken)));

        result.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent discount")
    public void testDeleteDiscount_NotFound() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/discounts/99999")
                .header("Authorization", createAuthorizationHeader(adminToken)));

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should fail to delete discount without admin role")
    public void testDeleteDiscount_Forbidden() throws Exception {
        ResultActions result = mockMvc.perform(delete("/api/discounts/" + testDiscount1.getId())
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isForbidden());
    }

    // ==================== Validate Discount Tests ====================

    @Test
    @DisplayName("Should validate valid discount code")
    public void testValidateDiscount_Success() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/validate")
                .param("code", "SAVE10")
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SAVE10"))
                .andExpect(jsonPath("$.percentage").value(10.00));
    }

    @Test
    @DisplayName("Should fail to validate expired discount code")
    public void testValidateDiscount_Expired() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/validate")
                .param("code", "EXPIRED")
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should fail to validate non-existent discount code")
    public void testValidateDiscount_NotFound() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/validate")
                .param("code", "NONEXISTENT")
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isBadRequest());
    }

    // ==================== Get Active Discounts Tests ====================

    @Test
    @DisplayName("Should get active discounts as authenticated user")
    public void testGetActiveDiscounts_Success() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/active")
                .header("Authorization", createAuthorizationHeader(userToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // Only SAVE10 and SAVE20, not EXPIRED
                .andExpect(jsonPath("$[0].code").value("SAVE10"))
                .andExpect(jsonPath("$[1].code").value("SAVE20"));
    }

    @Test
    @DisplayName("Should get active discounts as admin")
    public void testGetActiveDiscounts_AsAdmin() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/active")
                .header("Authorization", createAuthorizationHeader(adminToken)));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Should fail to get active discounts without authentication")
    public void testGetActiveDiscounts_Unauthorized() throws Exception {
        ResultActions result = mockMvc.perform(get("/api/discounts/active"));

        result.andExpect(status().isForbidden());
    }
}

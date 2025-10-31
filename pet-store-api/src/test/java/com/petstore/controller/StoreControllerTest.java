package com.petstore.controller;

import com.petstore.model.Cart;
import com.petstore.model.Order;
import com.petstore.model.Payment;
import com.petstore.model.User;
import com.petstore.model.Discount;
import com.petstore.service.CartService;
import com.petstore.service.OrderService;
import com.petstore.service.UserService;
import com.petstore.service.DiscountService;
import com.petstore.config.TestSecurityConfig;
import com.petstore.exception.CartEmptyException;
import com.petstore.exception.CartItemNotFoundException;
import com.petstore.exception.GlobalExceptionHandler;
import com.petstore.exception.OrderNotFoundException;
import com.petstore.exception.UserCartNotFoundException;
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
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * WebMvcTest for StoreController.
 * <p>
 * This test class covers all controller endpoints for store and order
 * management, including CRUD operations,
 * edge cases, negative scenarios, and security/authorization checks. It uses
 * MockMvc for HTTP request simulation
 * and mocks the service layer to isolate controller logic. Security filters are
 * enabled for realistic access control testing.
 */
@WebMvcTest(StoreController.class)
@Import({ GlobalExceptionHandler.class, TestSecurityConfig.class })
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Store Controller WebMvcTest")
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CartService cartService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private DiscountService discountService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test: GET /api/stores/orders - should return user's orders
     * Verifies that all orders are returned successfully for an authenticated user.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/stores/orders - should return user's orders")
    void shouldReturnUserOrders() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        Order order = new Order();
        order.setId(100L);
        when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(order));
        mockMvc.perform(get("/api/stores/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L));
    }

    /**
     * Test: GET /api/stores/orders - should return all orders for admin
     * Verifies that all orders are returned successfully for an authenticated
     * admin.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/stores/orders - should return all orders for admin")
    void shouldReturnAllOrdersForAdmin() throws Exception {
        User admin = new User();
        admin.setId(2L);
        admin.setEmail("admin@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(admin));
        Order order = new Order();
        order.setId(101L);
        when(orderService.getAllOrders()).thenReturn(List.of(order));
        mockMvc.perform(get("/api/stores/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101L));
    }

    /**
     * Test: POST /api/stores/cart/add/{petId} - should add pet to cart
     * Verifies that a pet is added to the user's cart successfully.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/stores/cart/add/{petId} - should add pet to cart")
    void shouldAddPetToCart() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        Cart cart = new Cart();
        cart.setId(200L);
        when(cartService.addPetToCart(1L, 10L)).thenReturn(cart);
        mockMvc.perform(post("/api/stores/cart/add/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value(200));
    }

    /**
     * Test: GET /api/stores/cart/{userId} - should return user's cart
     * Verifies that the user's cart is retrieved successfully.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/stores/cart/{userId} - should return user's cart")
    void shouldReturnUserCart() throws Exception {
        Cart cart = new Cart();
        cart.setId(201L);
        when(cartService.getCartByUserId(1L)).thenReturn(cart);
        mockMvc.perform(get("/api/stores/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value(201));
    }

    /**
     * Test: DELETE /api/stores/cart/item/{cartItemId} - should remove item from
     * cart
     * Verifies that an item is removed from the user's cart successfully.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/stores/cart/item/{cartItemId} - should remove item from cart")
    void shouldRemoveItemFromCart() throws Exception {
        mockMvc.perform(delete("/api/stores/cart/item/300"))
                .andExpect(status().isNoContent());
    }

    /**
     * Test: GET /api/stores/cart/discount/validate - should validate discount and
     * calculate new total
     * Verifies that a discount code is validated and the new total is calculated
     * correctly.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/stores/cart/discount/validate - should validate discount and calculate new total")
    void shouldValidateDiscountAndCalculateTotal() throws Exception {
        Discount discount = new Discount();
        discount.setCode("SAVE10");
        discount.setPercentage(BigDecimal.valueOf(10));
        when(discountService.validateDiscount("SAVE10")).thenReturn(discount);
        mockMvc.perform(get("/api/stores/cart/discount/validate?code=SAVE10&total=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".code").value("SAVE10"))
                .andExpect(jsonPath(".newTotal").value(90.0));
    }

    /**
     * Test: POST /api/stores/checkout - should checkout cart into order
     * Verifies that the user's cart is checked out successfully into an order.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/stores/checkout - should checkout cart into order")
    void shouldCheckoutCart() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        Order order = new Order();
        order.setId(400L);
        when(orderService.checkout(1L, null)).thenReturn(order);
        mockMvc.perform(post("/api/stores/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".id").value(400));
    }

    /**
     * Test: POST /api/stores/order/{orderId}/pay - should make payment for order
     * Verifies that payment is made successfully for the user's order.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/stores/order/{orderId}/pay - should make payment for order")
    void shouldMakePaymentForOrder() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        when(orderService.isOrderOwnedByUser(500L, 1L)).thenReturn(true);
        Payment payment = new Payment();
        payment.setId(501L);
        Map<String, Object> paymentOrderRequest = new HashMap<>();
        paymentOrderRequest.put("paymentType", "CREDIT_CARD");
        paymentOrderRequest.put("shippingAddressId", 1L);
        paymentOrderRequest.put("billingAddressId", 2L);
        paymentOrderRequest.put("paymentNote", "Test payment");
        mockMvc.perform(post("/api/stores/order/500/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentOrderRequest)))
                .andExpect(status().isOk());
    }

    /**
     * Test: DELETE /api/stores/order/{orderId} - should cancel order
     * Verifies that the user's order is cancelled successfully.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/stores/order/{orderId} - should cancel order")
    void shouldCancelOrder() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        when(orderService.isOrderOwnedByUser(600L, 1L)).thenReturn(true);
        mockMvc.perform(delete("/api/stores/order/600"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".message").value("Order cancelled successfully"));
    }

    /**
     * Test: DELETE /api/stores/order/{orderId}/delete - should delete order as
     * admin
     * Verifies that an admin can delete an order successfully.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/stores/order/{orderId}/delete - should delete order as admin")
    void shouldDeleteOrderAsAdmin() throws Exception {
        User admin = new User();
        admin.setId(2L);
        admin.setEmail("admin@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(admin));
        mockMvc.perform(delete("/api/stores/order/700/delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(".message").value("Order deleted successfully"));
    }

    /**
     * Test: PATCH /api/stores/order/{orderId}/delivery-status - should update
     * delivery status
     * Verifies that the delivery status of an order is updated successfully by an
     * admin.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/stores/order/{orderId}/delivery-status - should update delivery status")
    void shouldUpdateOrderDeliveryStatus() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("status", "DELIVERED");
        body.put("date", "2025-10-30");
        mockMvc.perform(patch("/api/stores/order/800/delivery-status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(".message").value("Order delivery status updated successfully"));
    }

    /**
     * Test: GET /api/stores/orders - should return 400 if user not found
     * Verifies that a 400 Bad Request is returned when the user is not found.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/stores/orders - should return 400 if user not found")
    void shouldReturn400IfUserNotFoundOnOrders() throws Exception {
        when(userService.getUserByEmail(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/stores/orders"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST /api/stores/cart/add/{petId} - should return 400 if user not found
     * Verifies that a 400 Bad Request is returned when the user is not found.
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/stores/cart/add/{petId} - should return 400 if user not found")
    void shouldReturn400IfUserNotFoundOnAddToCart() throws Exception {
        when(userService.getUserByEmail(any())).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/stores/cart/add/10"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: GET /api/stores/cart/{userId} - should return 404 if cart not found
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /api/stores/cart/{userId} - should return 404 if cart not found")
    void shouldReturn404IfCartNotFound() throws Exception {

        when(cartService.getCartByUserId(999L)).thenThrow(new UserCartNotFoundException(999L));
        mockMvc.perform(get("/api/stores/cart/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test: GET /api/stores/order/{orderId} - should return 404 if order not found
     */
    @Test
    @WithMockUser(roles = "USER", username = "user@example.com")
    @DisplayName("GET /api/stores/order/{orderId} - should return 404 if order not found")
    void shouldReturn404IfOrderNotFound() throws Exception {

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");

        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(mockUser));

        when(orderService.getOrderByIdAndUserId(999L, 1L))
                .thenThrow(new OrderNotFoundException(999L));

        mockMvc.perform(get("/api/stores/order/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test: DELETE /api/stores/cart/item/{cartItemId} - should return 404 if item
     * not found
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/stores/cart/item/{cartItemId} - should return 404 if item not found")
    void shouldReturn404IfCartItemNotFound() throws Exception {

        doThrow(new CartItemNotFoundException(999L)).when(cartService).removeCartItem(999L);
        mockMvc.perform(delete("/api/stores/cart/item/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test: POST /api/stores/checkout - should return 400 if cart is empty
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/stores/checkout - should return 400 if cart is empty")
    void shouldReturn400IfCartIsEmptyOnCheckout() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        doThrow(new CartEmptyException(1L)).when(orderService).checkout(eq(1L), any());
        mockMvc.perform(post("/api/stores/checkout"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: POST /api/stores/order/{orderId}/pay - should return 403 if user not
     * authorized
     */
    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/stores/order/{orderId}/pay - should return 403 if user not authorized")
    void shouldReturn403IfUserNotAuthorizedForPayment() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        when(userService.getUserByEmail(any())).thenReturn(Optional.of(user));
        when(orderService.isOrderOwnedByUser(999L, 1L)).thenReturn(false);
        Map<String, Object> paymentOrderRequest = new HashMap<>();
        paymentOrderRequest.put("paymentType", "CREDIT_CARD");
        paymentOrderRequest.put("shippingAddressId", 1L);
        paymentOrderRequest.put("billingAddressId", 2L);
        paymentOrderRequest.put("paymentNote", "Test payment");
        mockMvc.perform(post("/api/stores/order/999/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentOrderRequest)))
                .andExpect(status().isForbidden());
    }
}

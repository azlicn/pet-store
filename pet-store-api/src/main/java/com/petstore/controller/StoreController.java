package com.petstore.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.DeliveryStatus;
import com.petstore.exception.OrderOwnershipException;
import com.petstore.model.Cart;
import com.petstore.model.Discount;
import com.petstore.model.Order;
import com.petstore.model.Payment;
import com.petstore.model.User;
import com.petstore.service.CartService;
import com.petstore.service.DiscountService;
import com.petstore.service.OrderService;
import com.petstore.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/stores")
@Tag(name = "Store Controller", description = "Store and Order Management API")
public class StoreController {

    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);

    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;
    private final DiscountService discountService;

    public StoreController(UserService userService, CartService cartService, OrderService orderService,
            DiscountService discountService) {
        this.userService = userService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.discountService = discountService;
    }

    /*
     * Get all the orders for the authenticated user, if they are ADMIN role, return
     * all orders, else only their own orders
     * GET /api/stores/orders
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/orders")
        @Operation(summary = "Get orders", description = "Get all orders for the authenticated user. If ADMIN, returns all orders; else only their own.")
    public ResponseEntity<List<Order>> getOrders() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            logger.warn("User with email '{}' not found during getOrders request", userEmail);
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(orderService.getAllOrders());
        } else {
            return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
        }
    }

    /**
     * Add a pet to the user's cart.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cart/add/{petId}")
        @Operation(summary = "Add pet to cart", description = "Add a pet to the user's cart.")
    public ResponseEntity<Cart> addToCart(@PathVariable Long petId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            logger.warn("User with email '{}' not found during addToCart request", userEmail);
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        return ResponseEntity.ok(cartService.addPetToCart(user.getId(), petId));
    }

    /**
     * Get the user's cart.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/cart/{userId}")
        @Operation(summary = "Get user's cart", description = "Get the user's cart by user ID.")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    /**
     * Get the user's cart.
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/order/{orderId}")
        @Operation(summary = "Get order", description = "Get the user's order by order ID. If ADMIN, can get any order.")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            logger.warn("User with email '{}' not found during checkout request", userEmail);
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.ok(orderService.getOrderByIdAndUserId(orderId, user.getId()));
        }

        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    /**
     * Remove a specific item from the user's cart.
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/cart/item/{cartItemId}")
        @Operation(summary = "Remove item from cart", description = "Remove a specific item from the user's cart.")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartService.removeCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/cart/discount/validate")
        @Operation(summary = "Validate discount", description = "Validate a discount code for the user's cart.")
    public ResponseEntity<?> validateDiscount(@RequestParam String code, @RequestParam BigDecimal total) {

        Discount discount = discountService.validateDiscount(code);

        BigDecimal discountAmount = total.multiply(discount.getPercentage().divide(BigDecimal.valueOf(100)));
        BigDecimal newTotal = total.subtract(discountAmount);

        Map<String, Object> response = new HashMap<>();
        response.put("code", discount.getCode());
        response.put("percentage", discount.getPercentage());
        response.put("discountAmount", discountAmount);
        response.put("newTotal", newTotal);

        return ResponseEntity.ok(response);
    }

    /**
     * Checkout a user's cart into an order.
     * Allows optional discount code and optional address ID.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/checkout")
        @Operation(summary = "Checkout cart", description = "Checkout a user's cart into an order. Allows optional discount code.")
    public ResponseEntity<Order> checkout(
            @RequestParam(required = false) String discountCode) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            logger.warn("User with email '{}' not found during checkout request", userEmail);
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        Order order = orderService.checkout(user.getId(), discountCode);
        return ResponseEntity.ok(order);
    }

    /**
     * Make payment for a specific order.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/order/{orderId}/pay")
        @Operation(summary = "Make payment for order", description = "Make payment for a specific order.")
    public ResponseEntity<Payment> makePayment(@PathVariable Long orderId,
            @RequestBody PaymentOrderRequest paymentOrderRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            logger.warn("User with email '{}' not found during checkout request", userEmail);
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        // Verify the order belongs to this user
        if (!orderService.isOrderOwnedByUser(orderId, user.getId())) {
            throw new OrderOwnershipException(orderId, user.getId());
        }

        return ResponseEntity.ok(orderService.makePayment(orderId, paymentOrderRequest));
    }

    /**
     * Cancel a specific order
     * DELETE /api/stores/order/{orderId}
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/order/{orderId}")
        @Operation(summary = "Cancel order", description = "Cancel a specific order.")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            logger.warn("User with email '{}' not found during cancelOrder request", userEmail);
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        // Verify the order belongs to this user
        if (!orderService.isOrderOwnedByUser(orderId, user.getId())) {
            throw new OrderOwnershipException(orderId, user.getId());
        }

        orderService.cancelOrder(orderId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Order cancelled successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a specific order
     * DELETE /api/stores/order/{orderId}/delete
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/order/{orderId}/delete")
        @Operation(summary = "Delete order", description = "Delete a specific order. Admins can delete any order; users can delete their own.")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        Optional<User> userOptional = userService.getUserByEmail(userEmail);

        if (userOptional.isEmpty()) {
            logger.warn("User with email '{}' not found during deleteOrder request", userEmail);
            return ResponseEntity.badRequest().build();
        }

        User user = userOptional.get();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // Verify the order belongs to this user
            if (!orderService.isOrderOwnedByUser(orderId, user.getId())) {
                throw new OrderOwnershipException(orderId, user.getId());
            }
            orderService.deleteOrder(orderId);
        } else {
            // Admins can delete any order
            orderService.deleteOrder(orderId);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Order deleted successfully");

        return ResponseEntity.ok(response);
    }

    /*
     * Update delivery status of an order (ADMIN only)
     * PATCH /api/stores/order/{orderId}/delivery-status
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/order/{orderId}/delivery-status")
        @Operation(summary = "Update order delivery status", description = "Update delivery status of an order (ADMIN only).")
    public ResponseEntity<?> updateOrderDeliveryStatus(@PathVariable Long orderId,
            @RequestBody Map<String, String> body) {

        DeliveryStatus status = DeliveryStatus.valueOf(body.get("status"));
        String dateString = body.get("date");

        orderService.updateOrderDeliveryStatus(orderId, status, dateString);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order delivery status updated successfully");
        return ResponseEntity.ok(response);
    }

}

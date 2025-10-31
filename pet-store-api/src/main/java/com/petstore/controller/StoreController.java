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
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;


/**
 * REST controller for store and order management operations.
 * Provides endpoints for cart management, order processing, payment, discount validation, and delivery status updates.
 */
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

    /**
     * Retrieves all orders for the authenticated user. If the user is an admin, returns all orders; otherwise, only their own orders.
     *
     * @return ResponseEntity containing the list of orders
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
     * Adds a pet to the authenticated user's cart.
     *
     * @param petId the ID of the pet to add
     * @return ResponseEntity containing the updated cart
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
     * Retrieves the cart for the specified user ID.
     *
     * @param userId the ID of the user whose cart to retrieve
     * @return ResponseEntity containing the user's cart
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/cart/{userId}")
        @Operation(summary = "Get user's cart", description = "Get the user's cart by user ID.")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    /**
     * Retrieves the order for the specified order ID. Admins can get any order; users can get only their own.
     *
     * @param orderId the ID of the order to retrieve
     * @return ResponseEntity containing the order
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
     * Removes a specific item from the authenticated user's cart.
     *
     * @param cartItemId the ID of the cart item to remove
     * @return ResponseEntity with no content if successful
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/cart/item/{cartItemId}")
        @Operation(summary = "Remove item from cart", description = "Remove a specific item from the user's cart.")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {

        cartService.removeCartItem(cartItemId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Validates a discount code for the user's cart and calculates the new total.
     *
     * @param code the discount code to validate
     * @param total the current total amount
     * @return ResponseEntity containing discount details and new total
     */
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
     * Checks out the authenticated user's cart into an order. Allows optional discount code.
     *
     * @param discountCode optional discount code to apply
     * @return ResponseEntity containing the created order
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
     * Makes payment for a specific order.
     *
     * @param orderId the ID of the order to pay for
     * @param paymentOrderRequest the payment request details
     * @return ResponseEntity containing the payment information
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/order/{orderId}/pay")
        @Operation(summary = "Make payment for order", description = "Make payment for a specific order.")
    public ResponseEntity<Payment> makePayment(@PathVariable Long orderId,
            @Valid @RequestBody PaymentOrderRequest paymentOrderRequest) {

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
     * Cancels a specific order for the authenticated user.
     *
     * @param orderId the ID of the order to cancel
     * @return ResponseEntity containing a cancellation message
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
     * Deletes a specific order. Admins can delete any order; users can delete their own.
     *
     * @param orderId the ID of the order to delete
     * @return ResponseEntity containing a deletion message
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

    /**
     * Updates the delivery status of an order. Only admins can perform this operation.
     *
     * @param orderId the ID of the order to update
     * @param body the request body containing status and date
     * @return ResponseEntity containing a status update message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/order/{orderId}/delivery-status")
        @Operation(summary = "Update order delivery status", description = "Update delivery status of an order (ADMIN only).")
    public ResponseEntity<?> updateOrderDeliveryStatus(@PathVariable Long orderId,
            @Valid @RequestBody Map<String, String> body) {

        DeliveryStatus status = DeliveryStatus.valueOf(body.get("status"));
        String dateString = body.get("date");

        orderService.updateOrderDeliveryStatus(orderId, status, dateString);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Order delivery status updated successfully");
        
        return ResponseEntity.ok(response);
    }

}

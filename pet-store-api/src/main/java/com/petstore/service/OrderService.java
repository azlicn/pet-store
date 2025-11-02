package com.petstore.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.AuditOrderAction;
import com.petstore.enums.DeliveryStatus;
import com.petstore.enums.OrderStatus;
import com.petstore.enums.PaymentStatus;
import com.petstore.enums.PetStatus;
import com.petstore.exception.AddressNotFoundException;
import com.petstore.exception.CartEmptyException;
import com.petstore.exception.InvalidUserException;
import com.petstore.exception.OrderNotFoundException;
import com.petstore.exception.PetAlreadySoldException;
import com.petstore.exception.UserCartNotFoundException;
import com.petstore.generator.OrderNumberGenerator;
import com.petstore.model.Address;
import com.petstore.model.AuditLog;
import com.petstore.model.Cart;
import com.petstore.model.CartItem;
import com.petstore.model.Delivery;
import com.petstore.model.Discount;
import com.petstore.model.Order;
import com.petstore.model.OrderItem;
import com.petstore.model.Payment;
import com.petstore.model.Pet;
import com.petstore.repository.AddressRepository;
import com.petstore.repository.AuditLogRepository;
import com.petstore.repository.CartRepository;
import com.petstore.repository.DeliveryRepository;
import com.petstore.repository.OrderRepository;
import com.petstore.repository.PaymentRepository;
import com.petstore.repository.PetRepository;
import com.petstore.strategy.PaymentStrategyFactory;
import com.petstore.strategy.payment.PaymentStrategy;

import jakarta.transaction.Transactional;

/**
 * Service for managing orders in the store
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PetRepository petRepository;
    private final AuditLogRepository auditLogRepository;
    private final PaymentRepository paymentRepository;
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final DiscountService discountService;
    private final OrderNumberGenerator orderNumberGenerator;
    private final PaymentStrategyFactory paymentStrategyFactory;

    public OrderService(CartRepository cartRepository, OrderRepository orderRepository,
            PetRepository petRepository, AuditLogRepository auditLogRepository,
            PaymentRepository paymentRepository, DeliveryRepository deliveryRepository,
            AddressRepository addressRepository, DiscountService discountService, OrderNumberGenerator orderNumberGenerator, PaymentStrategyFactory paymentStrategyFactory) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.petRepository = petRepository;
        this.auditLogRepository = auditLogRepository;
        this.paymentRepository = paymentRepository;
        this.deliveryRepository = deliveryRepository;
        this.addressRepository = addressRepository;
        this.discountService = discountService;
        this.orderNumberGenerator = orderNumberGenerator;
        this.paymentStrategyFactory = paymentStrategyFactory;
    }

    /**
     * Retrieves all orders in the store.
     *
     * @return list of all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId the user ID
     * @return list of orders belonging to the user
     */
    public List<Order> getOrdersByUserId(Long userId) {

        if (userId == null) {
            throw new InvalidUserException("User ID cannot be null");
        }

        return orderRepository.findByUserId(userId);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the order ID
     * @return the order if found
     * @throws OrderNotFoundException if the order does not exist
     */
    public Order getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return order;
    }

    /**
     * Retrieves an order by its ID and user ID.
     *
     * @param orderId the order ID
     * @param userId  the user ID
     * @return the order if found
     * @throws OrderNotFoundException if the order does not exist for the user
     */
    public Order getOrderByIdAndUserId(Long orderId, Long userId) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId, userId));

        return order;
    }

    /**
     * Performs checkout for a user's cart, creating an order.
     *
     * @param userId       the user ID
     * @param discountCode the discount code to apply (optional)
     * @return the created order
     * @throws UserCartNotFoundException if the user's cart does not exist
     * @throws PetAlreadySoldException   if any pet in the cart is already sold
     */
    @Transactional
    public Order checkout(Long userId, String discountCode) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserCartNotFoundException(userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CartEmptyException(userId);
        }

        Order order = new Order();
        order.setOrderNumber(orderNumberGenerator.generate());
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PLACED);

        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply discount and capture snapshot values
        if (discountCode != null && !discountCode.isBlank()) {
            Discount discount = discountService.validateDiscount(discountCode);
            BigDecimal percent = discount.getPercentage().divide(BigDecimal.valueOf(100));
            BigDecimal discountAmount = total.multiply(percent);
            total = total.subtract(discountAmount);
            
            // Store discount reference for reporting/tracking
            order.setDiscount(discount);
            
            // Capture immutable snapshot of discount values at time of order creation
            // This prevents historical orders from being affected by future discount changes
            order.setDiscountCode(discount.getCode());
            order.setDiscountPercentage(discount.getPercentage());
            order.setDiscountAmount(discountAmount);
        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PLACED);

        // Check pet availability and create order items in one pass
        for (CartItem cartItem : cart.getItems()) {
            if (!petRepository.existsByIdAndStatus(cartItem.getPet().getId(), PetStatus.AVAILABLE)) {
                throw new PetAlreadySoldException(cartItem.getPet().getId());
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPet(cartItem.getPet());
            orderItem.setPrice(cartItem.getPrice());
            order.getItems().add(orderItem);
        }

        orderRepository.save(order);
        cartRepository.delete(cart); // empty cart after checkout

        AuditLog auditLog = new AuditLog(Order.class.getName(), order.getId(), order.getUser(),
                AuditOrderAction.CREATE_ORDER.name(), null, OrderStatus.PLACED.name());
        auditLogRepository.save(auditLog);

        return order;
    }

    /**
     * Makes a payment for an order.
     *
     * @param orderId             the order ID
     * @param paymentOrderRequest the payment request details
     * @return the created payment
     * @throws OrderNotFoundException   if the order does not exist
     * @throws AddressNotFoundException if the shipping or billing address does not
     *                                  exist
     */
    @Transactional
    public Payment makePayment(Long orderId, PaymentOrderRequest paymentOrderRequest) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(
            paymentOrderRequest.getPaymentType());
    
        logger.error("Using payment strategy: paymentOrderRequest {}", paymentOrderRequest);
        paymentStrategy.validatePayment(paymentOrderRequest);
        

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentType(paymentOrderRequest.getPaymentType());
       //payment.setPaymentNote(paymentOrderRequest.getPaymentNote());
        paymentStrategy.processPayment(payment, paymentOrderRequest);

        paymentRepository.save(payment);

        // Update pets
        for (OrderItem item : order.getItems()) {
            Pet pet = item.getPet();
            pet.setStatus(PetStatus.SOLD);
            pet.setOwner(order.getUser());
            petRepository.save(pet);

            auditLogRepository.save(new AuditLog(Pet.class.getName(), pet.getId(), order.getUser(),
                    "CHANGE_PET_STATUS", PetStatus.AVAILABLE.name(), PetStatus.SOLD.name()));
        }
        // Update order
        order.setStatus(OrderStatus.APPROVED);
        order.setShippingAddress(addressRepository.findById(paymentOrderRequest.getShippingAddressId())
                .orElseThrow(() -> new AddressNotFoundException(paymentOrderRequest.getShippingAddressId())));
        if (paymentOrderRequest.getBillingAddressId() != null) {
            order.setBillingAddress(addressRepository.findById(paymentOrderRequest.getBillingAddressId())
                    .orElseThrow(() -> new AddressNotFoundException(paymentOrderRequest.getBillingAddressId())));
        } else {
            order.setBillingAddress(order.getShippingAddress());
        }
        orderRepository.save(order);

        // Create delivery
        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setName(order.getShippingAddress().getFullName());
        delivery.setPhone(order.getShippingAddress().getPhoneNumber());
        delivery.setAddress(order.getShippingAddress().getFullAddress());
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setCreatedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(), order.getUser(),
                AuditOrderAction.CHECKOUT_ORDER.name(), OrderStatus.PLACED.name(), OrderStatus.APPROVED.name()));

        return payment;
    }

    /**
     * Cancels an order by its ID.
     *
     * @param orderId the order ID
     * @throws OrderNotFoundException if the order does not exist
     */
    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(), order.getUser(),
                AuditOrderAction.CANCEL_ORDER.name(), OrderStatus.PLACED.name(), OrderStatus.CANCELLED.name()));
    }

    /**
     * Deletes (marks as cancelled) an order by its ID.
     *
     * @param orderId the order ID
     * @throws OrderNotFoundException if the order does not exist
     */
    @Transactional
    public void deleteOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

    }

    /**
     * Updates the delivery status of an order.
     *
     * @param orderId    the order ID
     * @param newStatus  the new delivery status
     * @param dateString the date/time string for shipped/delivered (optional)
     * @throws OrderNotFoundException if the order does not exist
     */
    @Transactional
    public void updateOrderDeliveryStatus(Long orderId, DeliveryStatus newStatus, String dateString) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        Delivery delivery = order.getDelivery();

        if (newStatus == DeliveryStatus.SHIPPED) {
            delivery.setStatus(DeliveryStatus.SHIPPED);
            delivery.setShippedAt(dateString != null ? LocalDateTime.parse(dateString) : LocalDateTime.now());
        } else if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setStatus(DeliveryStatus.DELIVERED);
            delivery.setDeliveredAt(dateString != null ? LocalDateTime.parse(dateString) : LocalDateTime.now());
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
        }

        deliveryRepository.save(delivery);

        if (newStatus == DeliveryStatus.SHIPPED) {
            auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(), order.getUser(),
                    AuditOrderAction.UPDATE_DELIVERY_STATUS.name(), DeliveryStatus.PENDING.name(),
                    DeliveryStatus.SHIPPED.name()));
        } else if (newStatus == DeliveryStatus.DELIVERED) {
            auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(), order.getUser(),
                    AuditOrderAction.UPDATE_DELIVERY_STATUS.name(), DeliveryStatus.SHIPPED.name(),
                    DeliveryStatus.DELIVERED.name()));
        }
    }

    /**
     * Checks if an order is owned by a user.
     *
     * @param orderId the order ID
     * @param userId  the user ID
     * @return true if the order is owned by the user, false otherwise
     */
    public boolean isOrderOwnedByUser(Long orderId, Long userId) {

        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(userId))
                .orElse(false);
    }

    /**
     * Checks if an address is used in any order (shipping or billing).
     *
     * @param address the address to check
     * @return true if the address is used, false otherwise
     */
    public boolean existsByAddressUsed(Address address) {

        return orderRepository.existsByAddressUsed(address);
    }

}

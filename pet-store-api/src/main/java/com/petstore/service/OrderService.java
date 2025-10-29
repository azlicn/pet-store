package com.petstore.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.AuditOrderAction;
import com.petstore.enums.DeliveryStatus;
import com.petstore.enums.OrderStatus;
import com.petstore.enums.PaymentStatus;
import com.petstore.enums.PetStatus;
import com.petstore.exception.AddressNotFoundException;
import com.petstore.exception.OrderNotFoundException;
import com.petstore.exception.UserCartNotFoundException;
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
import com.petstore.util.OrderNumberGenerator;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PetRepository petRepository;
    private final AuditLogRepository auditLogRepository;
    private final PaymentRepository paymentRepository;
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final DiscountService discountService;

    public OrderService(CartRepository cartRepository, OrderRepository orderRepository,
            PetRepository petRepository, AuditLogRepository auditLogRepository,
            PaymentRepository paymentRepository, DeliveryRepository deliveryRepository,
            AddressRepository addressRepository, DiscountService discountService) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.petRepository = petRepository;
        this.auditLogRepository = auditLogRepository;
        this.paymentRepository = paymentRepository;
        this.deliveryRepository = deliveryRepository;
        this.addressRepository = addressRepository;
        this.discountService = discountService;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return order;
    }

    public Order getOrderByIdAndUserId(Long orderId, Long userId) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId, userId));

        return order;
    }

    @Transactional
    public Order checkout(Long userId, String discountCode) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new UserCartNotFoundException(userId));

        Order order = new Order();
        order.setOrderNumber(OrderNumberGenerator.generateOrderNumber());
        order.setUser(cart.getUser());
        order.setStatus(OrderStatus.PLACED);

        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply discount
        if (discountCode != null && !discountCode.isBlank()) {
            Discount discount = discountService.validateDiscount(discountCode);
            BigDecimal percent = discount.getPercentage().divide(BigDecimal.valueOf(100));
            BigDecimal discountAmount = total.multiply(percent);
            total = total.subtract(discountAmount);
            order.setDiscount(discount);
        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PLACED);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPet(cartItem.getPet());
            orderItem.setPrice(cartItem.getPrice());
            order.getItems().add(orderItem);
        }

        orderRepository.save(order);
        cartRepository.delete(cart); // empty cart after checkout

        auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(),
                AuditOrderAction.CREATE_ORDER.name(), null, OrderStatus.PLACED.name(), LocalDateTime.now()));

        return order;
    }

    @Transactional
    public Payment makePayment(Long orderId, PaymentOrderRequest paymentOrderRequest) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentType(paymentOrderRequest.getPaymentType());
        payment.setPaymentNote(paymentOrderRequest.getPaymentNote());

        paymentRepository.save(payment);

        // Update pets
        for (OrderItem item : order.getItems()) {
            Pet pet = item.getPet();
            pet.setStatus(PetStatus.SOLD);
            pet.setOwner(order.getUser());
            petRepository.save(pet);

            auditLogRepository.save(new AuditLog(Pet.class.getName(), pet.getId(),
                    "status", PetStatus.AVAILABLE.name(), PetStatus.SOLD.name(), LocalDateTime.now()));
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

        auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(),
                AuditOrderAction.CHECKOUT_ORDER.name(), OrderStatus.PLACED.name(), OrderStatus.APPROVED.name(),
                LocalDateTime.now()));

        return payment;
    }

    @Transactional
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(),
                AuditOrderAction.CANCEL_ORDER.name(), OrderStatus.PLACED.name(), OrderStatus.CANCELLED.name(),
                LocalDateTime.now()));
    }

    @Transactional
    public void deleteOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

    }

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
            auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(),
                    AuditOrderAction.UPDATE_DELIVERY_STATUS.name(), DeliveryStatus.PENDING.name(),
                    DeliveryStatus.SHIPPED.name(), LocalDateTime.now()));
        } else if (newStatus == DeliveryStatus.DELIVERED) {
            auditLogRepository.save(new AuditLog(Order.class.getName(), order.getId(),
                    AuditOrderAction.UPDATE_DELIVERY_STATUS.name(), DeliveryStatus.SHIPPED.name(),
                    DeliveryStatus.DELIVERED.name(), LocalDateTime.now()));
        }
    }

    public boolean isOrderOwnedByUser(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getId().equals(userId))
                .orElse(false);
    }

    public boolean existsByAddressUsed(Address address) {
        return orderRepository.existsByAddressUsed(address);
    }

}

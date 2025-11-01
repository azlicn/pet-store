package com.petstore.service;

import com.petstore.dto.PaymentOrderRequest;
import com.petstore.enums.DeliveryStatus;
import com.petstore.enums.WalletType;
import com.petstore.enums.OrderStatus;
import com.petstore.enums.PaymentStatus;
import com.petstore.enums.PaymentType;
import com.petstore.enums.PetStatus;
import com.petstore.exception.AddressNotFoundException;
import com.petstore.exception.InvalidPaymentException;
import com.petstore.exception.OrderNotFoundException;
import com.petstore.exception.PetAlreadySoldException;
import com.petstore.exception.UnsupportedPaymentException;
import com.petstore.exception.UserCartNotFoundException;
import com.petstore.generator.OrderNumberGenerator;
import com.petstore.model.*;
import com.petstore.repository.*;
import com.petstore.strategy.PaymentStrategyFactory;
import com.petstore.strategy.payment.PaymentStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.petstore.enums.PaymentType.CREDIT_CARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderService} covering order CRUD, checkout, payment,
 * delivery, and edge cases.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Tests")
class OrderServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private DiscountService discountService;
    @Mock
    private OrderNumberGenerator orderNumberGenerator;
    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;
    @Mock
    private PaymentStrategy paymentStrategy;
    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Cart testCart;
    private Pet testPet;
    private CartItem testCartItem;
    private Order testOrder;
    private Address testAddress;
    private Discount testDiscount;

    /**
     * Initializes test objects before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testCart = new Cart();
        testCart.setUser(testUser);
        testPet = new Pet();
        testPet.setId(100L);
        testPet.setStatus(PetStatus.AVAILABLE);
        testPet.setPrice(BigDecimal.valueOf(99.99));
        testCartItem = new CartItem();
        testCartItem.setPet(testPet);
        testCartItem.setPrice(testPet.getPrice());
        testCart.setItems(new ArrayList<>(List.of(testCartItem)));
        testOrder = new Order();
        testOrder.setId(10L);
        testOrder.setUser(testUser);
        testOrder.setStatus(OrderStatus.PLACED);
        testOrder.setItems(new ArrayList<>());
        testAddress = new Address();
        testAddress.setId(5L);
        testAddress.setFullName("John Doe");
        testAddress.setPhoneNumber("1234567890");
        testAddress.setStreet("123 Main St");
        testAddress.setCity("Metropolis");
        testAddress.setState("State");
        testAddress.setPostalCode("12345");
        testAddress.setCountry("Country");
        testDiscount = new Discount();
        testDiscount.setCode("SAVE10");
        testDiscount.setPercentage(BigDecimal.valueOf(10));
        testDiscount.setValidFrom(LocalDateTime.now().minusDays(1));
        testDiscount.setValidTo(LocalDateTime.now().plusDays(1));
        testDiscount.setActive(true);
    }

    /**
     * Tests retrieving all orders.
     */
    @Test
    void getAllOrders_ShouldReturnOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));
        List<Order> orders = orderService.getAllOrders();
        assertThat(orders).contains(testOrder);
        verify(orderRepository).findAll();
    }

    /**
     * Tests retrieving orders by user ID.
     */
    @Test
    void getOrdersByUserId_ShouldReturnOrders() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(testOrder));
        List<Order> orders = orderService.getOrdersByUserId(1L);
        assertThat(orders).contains(testOrder);
        verify(orderRepository).findByUserId(1L);
    }

    /**
     * Tests retrieving an order by ID.
     */
    @Test
    void getOrderById_ShouldReturnOrder() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        Order order = orderService.getOrderById(10L);
        assertThat(order).isEqualTo(testOrder);
        verify(orderRepository).findById(10L);
    }

    /**
     * Tests retrieving an order by ID when not found (edge case).
     */
    @Test
    void getOrderById_NotFound_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderRepository).findById(999L);
    }

    /**
     * Tests retrieving an order by ID and user ID.
     */
    @Test
    void getOrderByIdAndUserId_ShouldReturnOrder() {
        when(orderRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(testOrder));
        Order order = orderService.getOrderByIdAndUserId(10L, 1L);
        assertThat(order).isEqualTo(testOrder);
        verify(orderRepository).findByIdAndUserId(10L, 1L);
    }

    /**
     * Tests retrieving an order by ID and user ID when not found (edge case).
     */
    @Test
    void getOrderByIdAndUserId_NotFound_ShouldThrowException() {
        when(orderRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.getOrderByIdAndUserId(999L, 1L))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderRepository).findByIdAndUserId(999L, 1L);
    }

    /**
     * Tests successful checkout with discount.
     */
    @Test
    void checkout_ShouldCreateOrderWithDiscount() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(petRepository.existsByIdAndStatus(100L, PetStatus.AVAILABLE)).thenReturn(true);
        when(discountService.validateDiscount("SAVE10")).thenReturn(testDiscount);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(cartRepository).delete(any(Cart.class));
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(null);
        Order order = orderService.checkout(1L, "SAVE10");
        assertThat(order.getDiscount()).isEqualTo(testDiscount);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);
        verify(cartRepository).findByUserId(1L);
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).delete(testCart);
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    /**
     * Tests checkout when cart not found (edge case).
     */
    @Test
    void checkout_CartNotFound_ShouldThrowException() {
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.checkout(2L, null))
                .isInstanceOf(UserCartNotFoundException.class);
        verify(cartRepository).findByUserId(2L);
    }

    /**
     * Tests checkout when pet is already sold (edge case).
     */
    @Test
    void checkout_PetAlreadySold_ShouldThrowException() {
        testPet.setStatus(PetStatus.SOLD);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(petRepository.existsByIdAndStatus(100L, PetStatus.AVAILABLE)).thenReturn(false);
        assertThatThrownBy(() -> orderService.checkout(1L, null))
                .isInstanceOf(PetAlreadySoldException.class);
        verify(cartRepository).findByUserId(1L);
        verify(petRepository).existsByIdAndStatus(100L, PetStatus.AVAILABLE);
    }

    /**
     * Tests successful payment for an order.
     */
    @Test
    void makePayment_ShouldCreatePayment() {
        testOrder.setItems(new ArrayList<>());
        OrderItem orderItem = new OrderItem();
        orderItem.setPet(testPet);
        orderItem.setOrder(testOrder);
        testOrder.getItems().add(orderItem);
        testOrder.setTotalAmount(BigDecimal.valueOf(99.99));

        PaymentOrderRequest req = new PaymentOrderRequest();
        req.setPaymentType(PaymentType.CREDIT_CARD);
        req.setCardNumber("9876-5432-1098-7654");
        req.setPaymentNote("Paid");
        req.setShippingAddressId(5L);
        req.setBillingAddressId(5L);

        when(paymentStrategyFactory.getStrategy(PaymentType.CREDIT_CARD))
                .thenReturn(paymentStrategy);
        
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(addressRepository.findById(5L)).thenReturn(Optional.of(testAddress));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(null);
        when(petRepository.save(any(Pet.class))).thenReturn(testPet);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(null);

        Payment payment = orderService.makePayment(10L, req);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getOrder()).isEqualTo(testOrder);

        verify(orderRepository).findById(10L);
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void makePayment_WithDebitCard_ShouldCreatePayment() {
        // Arrange
        testOrder.setItems(new ArrayList<>());
        OrderItem orderItem = new OrderItem();
        orderItem.setPet(testPet);
        orderItem.setOrder(testOrder);
        testOrder.getItems().add(orderItem);
        testOrder.setTotalAmount(BigDecimal.valueOf(150.00));

        PaymentOrderRequest req = new PaymentOrderRequest();
        req.setPaymentType(PaymentType.DEBIT_CARD);
        req.setCardNumber("1234-5678-9012-3456");
        req.setPaymentNote("Debit payment");
        req.setShippingAddressId(5L);
        req.setBillingAddressId(5L);

        // Mock strategy
        when(paymentStrategyFactory.getStrategy(PaymentType.DEBIT_CARD))
                .thenReturn(paymentStrategy);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(addressRepository.findById(5L)).thenReturn(Optional.of(testAddress));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(null);

        // Act
        Payment payment = orderService.makePayment(10L, req);

        // Assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getPaymentType()).isEqualTo(PaymentType.DEBIT_CARD);
        verify(paymentStrategyFactory).getStrategy(PaymentType.DEBIT_CARD);
    }

    @Test
    void makePayment_WithEWallet_ShouldCreatePayment() {
        // Arrange
        testOrder.setItems(new ArrayList<>());
        OrderItem orderItem = new OrderItem();
        orderItem.setPet(testPet);
        orderItem.setOrder(testOrder);
        testOrder.getItems().add(orderItem);
        testOrder.setTotalAmount(BigDecimal.valueOf(200.00));

        PaymentOrderRequest req = new PaymentOrderRequest();
        req.setPaymentType(PaymentType.E_WALLET);
        req.setWalletType(WalletType.GRABPAY);
        req.setWalletId("+60123456789");
        req.setPaymentNote("E-Wallet payment");
        req.setShippingAddressId(5L);
        req.setBillingAddressId(5L);

        // Mock strategy
        when(paymentStrategyFactory.getStrategy(PaymentType.E_WALLET))
                .thenReturn(paymentStrategy);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(addressRepository.findById(5L)).thenReturn(Optional.of(testAddress));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(null);

        // Act
        Payment payment = orderService.makePayment(10L, req);

        // Assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getPaymentType()).isEqualTo(PaymentType.E_WALLET);
        verify(paymentStrategyFactory).getStrategy(PaymentType.E_WALLET);
    }

    @Test
    void makePayment_WithPayPal_ShouldCreatePayment() {
        // Arrange
        testOrder.setItems(new ArrayList<>());
        OrderItem orderItem = new OrderItem();
        orderItem.setPet(testPet);
        orderItem.setOrder(testOrder);
        testOrder.getItems().add(orderItem);
        testOrder.setTotalAmount(BigDecimal.valueOf(300.00));

        PaymentOrderRequest req = new PaymentOrderRequest();
        req.setPaymentType(PaymentType.PAYPAL);
        req.setPaypalId("user@example.com");
        req.setPaymentNote("PayPal payment");
        req.setShippingAddressId(5L);
        req.setBillingAddressId(5L);

        // Mock strategy
        when(paymentStrategyFactory.getStrategy(PaymentType.PAYPAL))
                .thenReturn(paymentStrategy);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(addressRepository.findById(5L)).thenReturn(Optional.of(testAddress));
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(null);

        // Act
        Payment payment = orderService.makePayment(10L, req);

        // Assert
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getPaymentType()).isEqualTo(PaymentType.PAYPAL);
        verify(paymentStrategyFactory).getStrategy(PaymentType.PAYPAL);
    }

    @Test
    void makePayment_WithUnsupportedPaymentType_ShouldThrowException() {
        // Arrange
        testOrder.setItems(new ArrayList<>());
        testOrder.setTotalAmount(BigDecimal.valueOf(99.99));

        PaymentOrderRequest req = new PaymentOrderRequest();
        req.setPaymentType(PaymentType.CREDIT_CARD);
        req.setShippingAddressId(5L);
        req.setBillingAddressId(5L);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(paymentStrategyFactory.getStrategy(PaymentType.CREDIT_CARD))
                .thenThrow(new UnsupportedPaymentException("Payment type not supported"));

        // Act & Assert
        assertThrows(UnsupportedPaymentException.class,
                () -> orderService.makePayment(10L, req));

        verify(paymentStrategyFactory).getStrategy(PaymentType.CREDIT_CARD);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void makePayment_WithInvalidPaymentData_ShouldThrowException() {
        // Arrange
        testOrder.setItems(new ArrayList<>());
        testOrder.setTotalAmount(BigDecimal.valueOf(99.99));

        PaymentOrderRequest req = new PaymentOrderRequest();
        req.setPaymentType(PaymentType.CREDIT_CARD);
        req.setShippingAddressId(5L);
        req.setBillingAddressId(5L);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(paymentStrategyFactory.getStrategy(PaymentType.CREDIT_CARD))
                .thenReturn(paymentStrategy);
        doThrow(new InvalidPaymentException("Invalid card number"))
                .when(paymentStrategy).validatePayment(any(PaymentOrderRequest.class));

        // Act & Assert
        assertThrows(InvalidPaymentException.class,
                () -> orderService.makePayment(10L, req));

        verify(paymentStrategy).validatePayment(req);
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    /**
     * Tests payment when order not found (edge case).
     */
    @Test
    void makePayment_OrderNotFound_ShouldThrowException() {
        PaymentOrderRequest req = new PaymentOrderRequest();
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.makePayment(999L, req))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderRepository).findById(999L);
    }

    /**
     * Tests payment when address not found (edge case).
     */
    // @Test
    void makePayment_AddressNotFound_ShouldThrowException() {
        testOrder.setItems(new ArrayList<>());
        OrderItem orderItem = new OrderItem();
        orderItem.setPet(testPet);
        orderItem.setOrder(testOrder);
        testOrder.getItems().add(orderItem);
        testOrder.setTotalAmount(BigDecimal.valueOf(99.99));
        PaymentOrderRequest req = new PaymentOrderRequest();
        req.setPaymentType(CREDIT_CARD);
        req.setPaymentNote("Paid");
        req.setShippingAddressId(999L);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.makePayment(10L, req))
                .isInstanceOf(AddressNotFoundException.class);
        verify(orderRepository).findById(10L);
        verify(addressRepository).findById(999L);
    }

    /**
     * Tests cancelling an order.
     */
    @Test
    void cancelOrder_ShouldCancelOrder() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(null);
        orderService.cancelOrder(10L);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).findById(10L);
        verify(orderRepository).save(testOrder);
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    /**
     * Tests cancelling an order that does not exist (edge case).
     */
    @Test
    void cancelOrder_OrderNotFound_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.cancelOrder(999L))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderRepository).findById(999L);
    }

    /**
     * Tests deleting an order.
     */
    @Test
    void deleteOrder_ShouldDeleteOrder() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        orderService.deleteOrder(10L);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).findById(10L);
        verify(orderRepository).save(testOrder);
    }

    /**
     * Tests deleting an order that does not exist (edge case).
     */
    @Test
    void deleteOrder_OrderNotFound_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.deleteOrder(999L))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderRepository).findById(999L);
    }

    /**
     * Tests updating order delivery status to SHIPPED.
     */
    @Test
    void updateOrderDeliveryStatus_Shipped_ShouldUpdateDelivery() {
        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.PENDING);
        testOrder.setDelivery(delivery);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(null);
        orderService.updateOrderDeliveryStatus(10L, DeliveryStatus.SHIPPED, null);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.SHIPPED);
        verify(orderRepository).findById(10L);
        verify(deliveryRepository).save(delivery);
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    /**
     * Tests updating order delivery status to DELIVERED.
     */
    @Test
    void updateOrderDeliveryStatus_Delivered_ShouldUpdateDeliveryAndOrder() {
        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.SHIPPED);
        testOrder.setDelivery(delivery);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        when(deliveryRepository.save(any(Delivery.class))).thenReturn(delivery);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(null);
        orderService.updateOrderDeliveryStatus(10L, DeliveryStatus.DELIVERED, null);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        verify(orderRepository).findById(10L);
        verify(deliveryRepository).save(delivery);
        verify(orderRepository).save(testOrder);
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    /**
     * Tests updating order delivery status when order not found (edge case).
     */
    @Test
    void updateOrderDeliveryStatus_OrderNotFound_ShouldThrowException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.updateOrderDeliveryStatus(999L, DeliveryStatus.SHIPPED, null))
                .isInstanceOf(OrderNotFoundException.class);
        verify(orderRepository).findById(999L);
    }

    /**
     * Tests isOrderOwnedByUser returns true for correct user.
     */
    @Test
    void isOrderOwnedByUser_ShouldReturnTrue() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        boolean owned = orderService.isOrderOwnedByUser(10L, 1L);
        assertThat(owned).isTrue();
        verify(orderRepository).findById(10L);
    }

    /**
     * Tests isOrderOwnedByUser returns false for wrong user.
     */
    @Test
    void isOrderOwnedByUser_ShouldReturnFalse() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(testOrder));
        boolean owned = orderService.isOrderOwnedByUser(10L, 2L);
        assertThat(owned).isFalse();
        verify(orderRepository).findById(10L);
    }

    /**
     * Tests existsByAddressUsed returns true when address is used.
     */
    @Test
    void existsByAddressUsed_ShouldReturnTrue() {
        when(orderRepository.existsByAddressUsed(testAddress)).thenReturn(true);
        boolean used = orderService.existsByAddressUsed(testAddress);
        assertThat(used).isTrue();
        verify(orderRepository).existsByAddressUsed(testAddress);
    }

    /**
     * Tests existsByAddressUsed returns false when address is not used.
     */
    @Test
    void existsByAddressUsed_ShouldReturnFalse() {
        when(orderRepository.existsByAddressUsed(testAddress)).thenReturn(false);
        boolean used = orderService.existsByAddressUsed(testAddress);
        assertThat(used).isFalse();
        verify(orderRepository).existsByAddressUsed(testAddress);
    }
}

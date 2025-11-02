package com.petstore.model;

import com.petstore.enums.DeliveryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Delivery Model Tests")
class DeliveryTest {

    private Delivery delivery;
    private Order testOrder;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        delivery = new Delivery();
        testOrder = new Order();
        testOrder.setId(1L);
        now = LocalDateTime.now();
        
        delivery.setName("John Doe");
        delivery.setPhone("+60123456789");
        delivery.setAddress("123 Main St, City, State 12345");
        delivery.setOrder(testOrder);
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setCreatedAt(now);
    }

    // Getter and Setter Tests
    
    @Test
    @DisplayName("Should get and set id")
    void shouldGetAndSetId() {
        delivery.setId(10L);
        assertThat(delivery.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should get and set order")
    void shouldGetAndSetOrder() {
        Order newOrder = new Order();
        newOrder.setId(2L);
        delivery.setOrder(newOrder);
        assertThat(delivery.getOrder()).isEqualTo(newOrder);
        assertThat(delivery.getOrder().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should get and set name")
    void shouldGetAndSetName() {
        delivery.setName("Jane Smith");
        assertThat(delivery.getName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("Should get and set phone")
    void shouldGetAndSetPhone() {
        delivery.setPhone("+60987654321");
        assertThat(delivery.getPhone()).isEqualTo("+60987654321");
    }

    @Test
    @DisplayName("Should get and set address")
    void shouldGetAndSetAddress() {
        delivery.setAddress("456 Oak Ave, Town, Province 67890");
        assertThat(delivery.getAddress()).isEqualTo("456 Oak Ave, Town, Province 67890");
    }

    @Test
    @DisplayName("Should get and set status")
    void shouldGetAndSetStatus() {
        delivery.setStatus(DeliveryStatus.SHIPPED);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.SHIPPED);
    }

    @Test
    @DisplayName("Should get and set createdAt")
    void shouldGetAndSetCreatedAt() {
        LocalDateTime createdTime = LocalDateTime.of(2025, 11, 1, 10, 30);
        delivery.setCreatedAt(createdTime);
        assertThat(delivery.getCreatedAt()).isEqualTo(createdTime);
    }

    @Test
    @DisplayName("Should get and set shippedAt")
    void shouldGetAndSetShippedAt() {
        LocalDateTime shippedTime = LocalDateTime.of(2025, 11, 2, 14, 0);
        delivery.setShippedAt(shippedTime);
        assertThat(delivery.getShippedAt()).isEqualTo(shippedTime);
    }

    @Test
    @DisplayName("Should get and set deliveredAt")
    void shouldGetAndSetDeliveredAt() {
        LocalDateTime deliveredTime = LocalDateTime.of(2025, 11, 3, 16, 45);
        delivery.setDeliveredAt(deliveredTime);
        assertThat(delivery.getDeliveredAt()).isEqualTo(deliveredTime);
    }

    // Default Values Tests
    
    @Test
    @DisplayName("Should have default PENDING status")
    void shouldHaveDefaultPendingStatus() {
        Delivery newDelivery = new Delivery();
        assertThat(newDelivery.getStatus()).isEqualTo(DeliveryStatus.PENDING);
    }

    @Test
    @DisplayName("Should have null shippedAt initially")
    void shouldHaveNullShippedAtInitially() {
        Delivery newDelivery = new Delivery();
        assertThat(newDelivery.getShippedAt()).isNull();
    }

    @Test
    @DisplayName("Should have null deliveredAt initially")
    void shouldHaveNullDeliveredAtInitially() {
        Delivery newDelivery = new Delivery();
        assertThat(newDelivery.getDeliveredAt()).isNull();
    }

    // Phone Number Tests
    
    @Test
    @DisplayName("Should handle international phone numbers")
    void shouldHandleInternationalPhoneNumbers() {
        delivery.setPhone("+1-555-123-4567");
        assertThat(delivery.getPhone()).isEqualTo("+1-555-123-4567");
    }

    @Test
    @DisplayName("Should handle local phone numbers")
    void shouldHandleLocalPhoneNumbers() {
        delivery.setPhone("0123456789");
        assertThat(delivery.getPhone()).isEqualTo("0123456789");
    }

    @Test
    @DisplayName("Should handle phone with parentheses format")
    void shouldHandlePhoneWithParentheses() {
        delivery.setPhone("(012) 345-6789");
        assertThat(delivery.getPhone()).isEqualTo("(012) 345-6789");
    }

    // Address Tests
    
    @Test
    @DisplayName("Should handle short address")
    void shouldHandleShortAddress() {
        delivery.setAddress("Unit 5A");
        assertThat(delivery.getAddress()).isEqualTo("Unit 5A");
    }

    @Test
    @DisplayName("Should handle long address with multiple lines")
    void shouldHandleLongAddress() {
        String longAddress = "Apartment 123, Tower B, The Grand Residences, " +
                           "456 Main Boulevard, City Center, State 12345, Country";
        delivery.setAddress(longAddress);
        assertThat(delivery.getAddress()).isEqualTo(longAddress);
    }

    @Test
    @DisplayName("Should handle address with special characters")
    void shouldHandleAddressWithSpecialCharacters() {
        delivery.setAddress("123-A, O'Connor St., Apt #4B");
        assertThat(delivery.getAddress()).isEqualTo("123-A, O'Connor St., Apt #4B");
    }

    // Name Tests
    
    @Test
    @DisplayName("Should handle short name")
    void shouldHandleShortName() {
        delivery.setName("Li");
        assertThat(delivery.getName()).isEqualTo("Li");
    }

    @Test
    @DisplayName("Should handle long name")
    void shouldHandleLongName() {
        delivery.setName("Muhammad Abdullah bin Abdul Rahman Al-Rashid");
        assertThat(delivery.getName()).isEqualTo("Muhammad Abdullah bin Abdul Rahman Al-Rashid");
    }

    @Test
    @DisplayName("Should handle name with special characters")
    void shouldHandleNameWithSpecialCharacters() {
        delivery.setName("O'Brien-Smith");
        assertThat(delivery.getName()).isEqualTo("O'Brien-Smith");
    }

    // Status Progression Tests
    
    @Test
    @DisplayName("Should track delivery progression from pending to shipped")
    void shouldTrackPendingToShipped() {
        delivery.setStatus(DeliveryStatus.PENDING);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PENDING);
        
        LocalDateTime shipTime = LocalDateTime.now();
        delivery.setStatus(DeliveryStatus.SHIPPED);
        delivery.setShippedAt(shipTime);
        
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.SHIPPED);
        assertThat(delivery.getShippedAt()).isEqualTo(shipTime);
    }

    @Test
    @DisplayName("Should track delivery progression from shipped to delivered")
    void shouldTrackShippedToDelivered() {
        LocalDateTime shipTime = LocalDateTime.now().minusDays(2);
        LocalDateTime deliverTime = LocalDateTime.now();
        
        delivery.setStatus(DeliveryStatus.SHIPPED);
        delivery.setShippedAt(shipTime);
        
        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(deliverTime);
        
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(delivery.getShippedAt()).isEqualTo(shipTime);
        assertThat(delivery.getDeliveredAt()).isEqualTo(deliverTime);
        assertThat(delivery.getDeliveredAt()).isAfter(delivery.getShippedAt());
    }

    @Test
    @DisplayName("Should handle complete delivery lifecycle")
    void shouldHandleCompleteLifecycle() {
        LocalDateTime created = LocalDateTime.now().minusDays(3);
        LocalDateTime shipped = LocalDateTime.now().minusDays(2);
        LocalDateTime delivered = LocalDateTime.now();
        
        delivery.setCreatedAt(created);
        delivery.setStatus(DeliveryStatus.PENDING);
        
        delivery.setStatus(DeliveryStatus.SHIPPED);
        delivery.setShippedAt(shipped);
        
        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(delivered);
        
        assertThat(delivery.getCreatedAt()).isEqualTo(created);
        assertThat(delivery.getShippedAt()).isEqualTo(shipped);
        assertThat(delivery.getDeliveredAt()).isEqualTo(delivered);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(delivery.getShippedAt()).isAfter(delivery.getCreatedAt());
        assertThat(delivery.getDeliveredAt()).isAfter(delivery.getShippedAt());
    }

    // Business Scenario Tests
    
    @Test
    @DisplayName("Should represent same-day delivery")
    void shouldRepresentSameDayDelivery() {
        LocalDateTime morning = LocalDateTime.of(2025, 11, 2, 9, 0);
        LocalDateTime afternoon = LocalDateTime.of(2025, 11, 2, 15, 30);
        
        delivery.setCreatedAt(morning);
        delivery.setShippedAt(morning.plusHours(1));
        delivery.setDeliveredAt(afternoon);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        
        assertThat(delivery.getCreatedAt().toLocalDate())
            .isEqualTo(delivery.getDeliveredAt().toLocalDate());
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
    }

    @Test
    @DisplayName("Should represent express delivery (1-2 days)")
    void shouldRepresentExpressDelivery() {
        LocalDateTime orderTime = LocalDateTime.of(2025, 11, 1, 10, 0);
        LocalDateTime deliveryTime = LocalDateTime.of(2025, 11, 2, 14, 0);
        
        delivery.setCreatedAt(orderTime);
        delivery.setShippedAt(orderTime.plusHours(4));
        delivery.setDeliveredAt(deliveryTime);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        
        assertThat(delivery.getDeliveredAt()).isBefore(orderTime.plusDays(3));
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
    }

    @Test
    @DisplayName("Should represent standard delivery (3-5 days)")
    void shouldRepresentStandardDelivery() {
        LocalDateTime orderTime = LocalDateTime.of(2025, 11, 1, 10, 0);
        LocalDateTime shipTime = LocalDateTime.of(2025, 11, 2, 9, 0);
        LocalDateTime deliveryTime = LocalDateTime.of(2025, 11, 5, 16, 0);
        
        delivery.setCreatedAt(orderTime);
        delivery.setShippedAt(shipTime);
        delivery.setDeliveredAt(deliveryTime);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        
        assertThat(delivery.getDeliveredAt()).isAfter(orderTime.plusDays(3));
        assertThat(delivery.getDeliveredAt()).isBefore(orderTime.plusDays(6));
    }

    @Test
    @DisplayName("Should represent pending delivery awaiting shipment")
    void shouldRepresentPendingDelivery() {
        delivery.setCreatedAt(LocalDateTime.now().minusHours(2));
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setShippedAt(null);
        delivery.setDeliveredAt(null);
        
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PENDING);
        assertThat(delivery.getShippedAt()).isNull();
        assertThat(delivery.getDeliveredAt()).isNull();
        assertThat(delivery.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should represent in-transit delivery")
    void shouldRepresentInTransitDelivery() {
        delivery.setCreatedAt(LocalDateTime.now().minusDays(1));
        delivery.setShippedAt(LocalDateTime.now().minusHours(12));
        delivery.setStatus(DeliveryStatus.SHIPPED);
        delivery.setDeliveredAt(null);
        
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.SHIPPED);
        assertThat(delivery.getShippedAt()).isNotNull();
        assertThat(delivery.getDeliveredAt()).isNull();
    }

    // Edge Case Tests
    
    @Test
    @DisplayName("Should handle null order initially")
    void shouldHandleNullOrderInitially() {
        Delivery newDelivery = new Delivery();
        assertThat(newDelivery.getOrder()).isNull();
    }

    @Test
    @DisplayName("Should handle null name initially")
    void shouldHandleNullNameInitially() {
        Delivery newDelivery = new Delivery();
        assertThat(newDelivery.getName()).isNull();
    }

    @Test
    @DisplayName("Should handle null phone initially")
    void shouldHandleNullPhoneInitially() {
        Delivery newDelivery = new Delivery();
        assertThat(newDelivery.getPhone()).isNull();
    }

    @Test
    @DisplayName("Should handle null address initially")
    void shouldHandleNullAddressInitially() {
        Delivery newDelivery = new Delivery();
        assertThat(newDelivery.getAddress()).isNull();
    }

    @Test
    @DisplayName("Should handle all DeliveryStatus enum values")
    void shouldHandleAllStatusValues() {
        delivery.setStatus(DeliveryStatus.PENDING);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PENDING);
        
        delivery.setStatus(DeliveryStatus.SHIPPED);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.SHIPPED);
        
        delivery.setStatus(DeliveryStatus.DELIVERED);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
    }

    @Test
    @DisplayName("Should maintain order reference")
    void shouldMaintainOrderReference() {
        Order order1 = new Order();
        order1.setId(100L);
        
        delivery.setOrder(order1);
        assertThat(delivery.getOrder()).isSameAs(order1);
        assertThat(delivery.getOrder().getId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should handle timestamps in different timezones conceptually")
    void shouldHandleTimestamps() {
        LocalDateTime timestamp1 = LocalDateTime.of(2025, 11, 2, 8, 0, 0);
        LocalDateTime timestamp2 = LocalDateTime.of(2025, 11, 2, 20, 0, 0);
        
        delivery.setCreatedAt(timestamp1);
        delivery.setShippedAt(timestamp1.plusHours(2));
        delivery.setDeliveredAt(timestamp2);
        
        assertThat(delivery.getCreatedAt()).isBefore(delivery.getShippedAt());
        assertThat(delivery.getShippedAt()).isBefore(delivery.getDeliveredAt());
    }

    @Test
    @DisplayName("Should allow status change back to pending (edge case)")
    void shouldAllowStatusChangeBackToPending() {
        delivery.setStatus(DeliveryStatus.SHIPPED);
        delivery.setStatus(DeliveryStatus.PENDING);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PENDING);
    }

    @Test
    @DisplayName("Should handle delivery with minimal information")
    void shouldHandleMinimalInformation() {
        Delivery minimal = new Delivery();
        minimal.setName("User");
        minimal.setPhone("123");
        minimal.setAddress("Home");
        
        assertThat(minimal.getName()).isEqualTo("User");
        assertThat(minimal.getPhone()).isEqualTo("123");
        assertThat(minimal.getAddress()).isEqualTo("Home");
        assertThat(minimal.getStatus()).isEqualTo(DeliveryStatus.PENDING);
    }

    @Test
    @DisplayName("Should handle delivery with complete information")
    void shouldHandleCompleteInformation() {
        LocalDateTime created = LocalDateTime.now().minusDays(5);
        LocalDateTime shipped = LocalDateTime.now().minusDays(3);
        LocalDateTime delivered = LocalDateTime.now().minusDays(1);
        
        Delivery complete = new Delivery();
        complete.setId(999L);
        complete.setOrder(testOrder);
        complete.setName("Jane Doe Smith");
        complete.setPhone("+60-12-345-6789");
        complete.setAddress("Unit 10B, Tower 2, Residence Park, 789 Boulevard Avenue, City 54321");
        complete.setStatus(DeliveryStatus.DELIVERED);
        complete.setCreatedAt(created);
        complete.setShippedAt(shipped);
        complete.setDeliveredAt(delivered);
        
        assertThat(complete.getId()).isEqualTo(999L);
        assertThat(complete.getOrder()).isEqualTo(testOrder);
        assertThat(complete.getName()).isEqualTo("Jane Doe Smith");
        assertThat(complete.getPhone()).isEqualTo("+60-12-345-6789");
        assertThat(complete.getAddress()).contains("Unit 10B");
        assertThat(complete.getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(complete.getCreatedAt()).isEqualTo(created);
        assertThat(complete.getShippedAt()).isEqualTo(shipped);
        assertThat(complete.getDeliveredAt()).isEqualTo(delivered);
    }
}

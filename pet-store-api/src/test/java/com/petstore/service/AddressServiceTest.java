package com.petstore.service;

import com.petstore.exception.AddressInUseException;
import com.petstore.exception.AddressNotFoundException;
import com.petstore.exception.UserNotFoundException;
import com.petstore.model.Address;
import com.petstore.model.User;
import com.petstore.repository.AddressRepository;
import com.petstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AddressService} covering address CRUD operations, edge cases, and exception scenarios.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Address Service Tests")
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private AddressService addressService;

    private User testUser;
    private Address testAddress;

    /**
     * Initializes test user and address before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testAddress = new Address();
        testAddress.setId(10L);
        testAddress.setUser(testUser);
        testAddress.setFullName("John Doe");
        testAddress.setPhoneNumber("1234567890");
        testAddress.setStreet("123 Main St");
        testAddress.setCity("Metropolis");
        testAddress.setState("State");
        testAddress.setPostalCode("12345");
        testAddress.setCountry("Country");
        testAddress.setDefault(false);
    }

    /**
     * Tests retrieving all addresses for a user.
     */
    @Test
    void getUserAddresses_ShouldReturnAddresses() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUser(testUser)).thenReturn(Arrays.asList(testAddress));
        List<Address> addresses = addressService.getUserAddresses(1L);
        assertThat(addresses).hasSize(1).contains(testAddress);
        verify(userRepository).findById(1L);
        verify(addressRepository).findByUser(testUser);
    }

    /**
     * Tests retrieving addresses for a non-existent user (edge case).
     */
    @Test
    void getUserAddresses_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> addressService.getUserAddresses(999L))
            .isInstanceOf(UserNotFoundException.class);
        verify(userRepository).findById(999L);
        verify(addressRepository, never()).findByUser(any());
    }

    /**
     * Tests creating a new address for a user, first address should be default.
     */
    @Test
    void createAddress_FirstAddress_ShouldBeDefault() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUser(testUser)).thenReturn(Collections.emptyList());
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Address address = new Address();
        address.setFullName("Jane Doe");
        Address created = addressService.createAddress(1L, address);
        assertThat(created.isDefault()).isTrue();
        verify(addressRepository).save(address);
    }

    /**
     * Tests creating a new address for a user when addresses already exist.
     */
    @Test
    void createAddress_NotFirstAddress_ShouldNotBeDefault() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUser(testUser)).thenReturn(Arrays.asList(testAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Address address = new Address();
        address.setFullName("Jane Doe");
        Address created = addressService.createAddress(1L, address);
        assertThat(created.isDefault()).isFalse();
        verify(addressRepository).save(address);
    }

    /**
     * Tests creating an address for a non-existent user (edge case).
     */
    @Test
    void createAddress_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Address address = new Address();
        assertThatThrownBy(() -> addressService.createAddress(999L, address))
            .isInstanceOf(UserNotFoundException.class);
        verify(userRepository).findById(999L);
        verify(addressRepository, never()).save(any(Address.class));
    }

    /**
     * Tests updating an address with valid data.
     */
    @Test
    void updateAddress_ShouldUpdateFields() {
        Address newAddress = new Address();
        newAddress.setFullName("Jane Doe");
        newAddress.setPhoneNumber("9876543210");
        newAddress.setStreet("456 Elm St");
        newAddress.setCity("Gotham");
        newAddress.setState("NewState");
        newAddress.setPostalCode("54321");
        newAddress.setCountry("NewCountry");
        newAddress.setDefault(true);
        when(addressRepository.findById(10L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Address updated = addressService.updateAddress(10L, newAddress);
        assertThat(updated.getFullName()).isEqualTo("Jane Doe");
        assertThat(updated.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(updated.getStreet()).isEqualTo("456 Elm St");
        assertThat(updated.getCity()).isEqualTo("Gotham");
        assertThat(updated.getState()).isEqualTo("NewState");
        assertThat(updated.getPostalCode()).isEqualTo("54321");
        assertThat(updated.getCountry()).isEqualTo("NewCountry");
        assertThat(updated.isDefault()).isTrue();
        verify(addressRepository).findById(10L);
        verify(addressRepository).save(testAddress);
    }

    /**
     * Tests updating a non-existent address (edge case).
     */
    @Test
    void updateAddress_AddressNotFound_ShouldThrowException() {
        Address newAddress = new Address();
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> addressService.updateAddress(999L, newAddress))
            .isInstanceOf(AddressNotFoundException.class);
        verify(addressRepository).findById(999L);
        verify(addressRepository, never()).save(any(Address.class));
    }

    /**
     * Tests deleting an address that is not used in any order.
     */
    @Test
    void deleteAddress_ShouldDeleteIfNotUsed() {
        when(addressRepository.findById(10L)).thenReturn(Optional.of(testAddress));
        when(orderService.existsByAddressUsed(testAddress)).thenReturn(false);
        addressService.deleteAddress(10L);
        verify(addressRepository).findById(10L);
        verify(orderService).existsByAddressUsed(testAddress);
        verify(addressRepository).deleteById(10L);
    }

    /**
     * Tests deleting an address that is used in an order (edge case).
     */
    @Test
    void deleteAddress_AddressInUse_ShouldThrowException() {
        when(addressRepository.findById(10L)).thenReturn(Optional.of(testAddress));
        when(orderService.existsByAddressUsed(testAddress)).thenReturn(true);
        assertThatThrownBy(() -> addressService.deleteAddress(10L))
            .isInstanceOf(AddressInUseException.class);
        verify(addressRepository).findById(10L);
        verify(orderService).existsByAddressUsed(testAddress);
        verify(addressRepository, never()).deleteById(any(Long.class));
    }

    /**
     * Tests deleting a non-existent address (edge case).
     */
    @Test
    void deleteAddress_AddressNotFound_ShouldThrowException() {
        when(addressRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> addressService.deleteAddress(999L))
            .isInstanceOf(AddressNotFoundException.class);
        verify(addressRepository).findById(999L);
        verify(orderService, never()).existsByAddressUsed(any(Address.class));
        verify(addressRepository, never()).deleteById(any(Long.class));
    }
}

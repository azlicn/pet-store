package com.petstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.petstore.exception.AddressInUseException;
import com.petstore.exception.AddressNotFoundException;
import com.petstore.exception.UserNotFoundException;
import com.petstore.model.Address;
import com.petstore.model.User;
import com.petstore.repository.AddressRepository;
import com.petstore.repository.UserRepository;

/**
 * Service for managing addresses in the store
 */
@Service
public class AddressService {

    private final AddressRepository addressRepository;

    private final UserRepository userRepository;

    private final OrderService orderService;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository, OrderService orderService) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    /**
     * Retrieves all addresses for a user
     *
     * @param userId the user ID
     * @return list of addresses belonging to the user
     */
    public List<Address> getUserAddresses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return addressRepository.findByUser(user);
    }

    /**
     * Creates a new address for a user
     *
     * @param userId the user ID
     * @param address the address to create
     * @return the created address
     */
    public Address createAddress(Long userId, Address address) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        address.setUser(user);

        // If user has no default, set first address as default
        if (addressRepository.findByUser(user).isEmpty()) {
            address.setDefault(true);
        }
        return addressRepository.save(address);
    }

    public Address updateAddress(Long addressId, Address newAddress) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        address.setFullName(newAddress.getFullName());
        address.setPhoneNumber(newAddress.getPhoneNumber());
        address.setStreet(newAddress.getStreet());
        address.setCity(newAddress.getCity());
        address.setState(newAddress.getState());
        address.setPostalCode(newAddress.getPostalCode());
        address.setCountry(newAddress.getCountry());
        address.setDefault(newAddress.isDefault());

        return addressRepository.save(address);
    }

    public void deleteAddress(Long addressId) {
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        boolean usedInOrder = orderService.existsByAddressUsed(address);
        if (usedInOrder) {
            throw new AddressInUseException(addressId);
        }
        addressRepository.deleteById(addressId);
    }

}

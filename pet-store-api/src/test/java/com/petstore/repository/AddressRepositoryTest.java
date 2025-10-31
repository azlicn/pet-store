package com.petstore.repository;

import com.petstore.model.Address;
import com.petstore.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Address Repository Tests")
class AddressRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    private User testUser;
    private Address address1;
    private Address address2;

    @BeforeEach
    void setUp() {
    testUser = new User();
    testUser.setEmail("test@example.com");
    testUser.setFirstName("Test");
    testUser.setLastName("User");
    testUser.setPassword("password");
    testUser = entityManager.persistAndFlush(testUser);

    address1 = new Address();
    address1.setUser(testUser);
    address1.setFullName("Test User");
    address1.setPhoneNumber("1234567890");
    address1.setStreet("123 Main St");
    address1.setCity("Springfield");
    address1.setState("IL");
    address1.setPostalCode("11111");
    address1.setCountry("USA");
    address1 = entityManager.persistAndFlush(address1);

    address2 = new Address();
    address2.setUser(testUser);
    address2.setFullName("Test User");
    address2.setPhoneNumber("0987654321");
    address2.setStreet("456 Elm St");
    address2.setCity("Springfield");
    address2.setState("IL");
    address2.setPostalCode("22222");
    address2.setCountry("USA");
    address2 = entityManager.persistAndFlush(address2);

    entityManager.clear();
    }

    @Test
    @DisplayName("Find by user - Should return addresses for user")
    void findByUser_ShouldReturnAddressesForUser() {
    List<Address> addresses = addressRepository.findByUser(testUser);
    assertThat(addresses).hasSize(2);
    assertThat(addresses).extracting(Address::getStreet)
        .containsExactlyInAnyOrder("123 Main St", "456 Elm St");
    }

    @Test
    @DisplayName("Find by user - Should return empty list for unknown user")
    void findByUser_ShouldReturnEmptyListForUnknownUser() {
    User unknownUser = new User();
    unknownUser.setEmail("unknown@example.com");
    unknownUser.setFirstName("Unknown");
    unknownUser.setLastName("User");
    unknownUser.setPassword("password");
    unknownUser = entityManager.persistAndFlush(unknownUser);

    List<Address> addresses = addressRepository.findByUser(unknownUser);
    assertThat(addresses).isEmpty();
    }
}

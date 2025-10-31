package com.petstore.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.petstore.model.Address;
import com.petstore.model.User;

/**
 * Repository for managing address entities in the database
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

        /**
         * Finds addresses by user
         *
         * @param user the user whose addresses to search for
         * @return list of addresses belonging to the given user
         */
        List<Address> findByUser(User user);

}

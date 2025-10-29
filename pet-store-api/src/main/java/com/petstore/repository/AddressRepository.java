package com.petstore.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.petstore.model.Address;
import com.petstore.model.User;

public interface AddressRepository extends JpaRepository<Address, Long> {

        List<Address> findByUser(User user);

}

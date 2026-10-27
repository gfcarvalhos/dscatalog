package com.gabrielsilva.dscatalog.repositories;

import com.gabrielsilva.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}

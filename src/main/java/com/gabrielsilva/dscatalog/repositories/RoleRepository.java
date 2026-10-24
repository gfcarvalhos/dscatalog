package com.gabrielsilva.dscatalog.repositories;

import com.gabrielsilva.dscatalog.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}

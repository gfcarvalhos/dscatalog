package com.gabrielsilva.dscatalog.repositories;

import com.gabrielsilva.dscatalog.entities.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRecoverRepository extends JpaRepository<PasswordRecover, Long> {
}

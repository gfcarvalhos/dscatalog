package com.gabrielsilva.dscatalog.repositories;

import com.gabrielsilva.dscatalog.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

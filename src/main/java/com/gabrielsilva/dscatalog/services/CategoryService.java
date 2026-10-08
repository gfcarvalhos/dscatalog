package com.gabrielsilva.dscatalog.services;

import com.gabrielsilva.dscatalog.dto.CategoryDTO;
import com.gabrielsilva.dscatalog.entities.Category;
import com.gabrielsilva.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> result = repository.findAll();
        return result.stream().map(CategoryDTO::new).toList();
    }
}

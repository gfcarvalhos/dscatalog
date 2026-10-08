package com.gabrielsilva.dscatalog.services;

import com.gabrielsilva.dscatalog.dto.CategoryDTO;
import com.gabrielsilva.dscatalog.entities.Category;
import com.gabrielsilva.dscatalog.repositories.CategoryRepository;
import com.gabrielsilva.dscatalog.services.exceptions.ObjectNotFoundException;
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

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category cat = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Categoria não encontrada"));
        return new CategoryDTO(cat);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        copyDtoToEntity(dto, entity);

        entity = repository.save(entity);

        return new CategoryDTO(entity);
    }


    private void copyDtoToEntity(CategoryDTO dto, Category entity){
        entity.setId(dto.getId());
        entity.setName(dto.getName());
    }
}

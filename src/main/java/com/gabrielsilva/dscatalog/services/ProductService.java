package com.gabrielsilva.dscatalog.services;

import com.gabrielsilva.dscatalog.dto.CategoryDTO;
import com.gabrielsilva.dscatalog.dto.ProductDTO;
import com.gabrielsilva.dscatalog.entities.Category;
import com.gabrielsilva.dscatalog.entities.Product;
import com.gabrielsilva.dscatalog.repositories.CategoryRepository;
import com.gabrielsilva.dscatalog.repositories.ProductRepository;
import com.gabrielsilva.dscatalog.services.exceptions.DatabaseException;
import com.gabrielsilva.dscatalog.services.exceptions.ObjectNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        List<Product> result = repository.findAll();
        return result.stream().map(ProductDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<Product> result = repository.findAll(pageable);
        return result.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product cat = repository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Produto não encontrado"));
        return new ProductDTO(cat, cat.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);

        entity = repository.save(entity);

        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            return new ProductDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException("Produto não encontrado");
        }
    }

    @Transactional
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new ObjectNotFoundException("Produto não encontrado");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity){
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        }
    }
}

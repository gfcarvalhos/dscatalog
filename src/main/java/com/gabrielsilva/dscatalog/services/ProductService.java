package com.gabrielsilva.dscatalog.services;

import com.gabrielsilva.dscatalog.dto.CategoryDTO;
import com.gabrielsilva.dscatalog.dto.ProductDTO;
import com.gabrielsilva.dscatalog.entities.Category;
import com.gabrielsilva.dscatalog.entities.Product;
import com.gabrielsilva.dscatalog.projections.ProductProjection;
import com.gabrielsilva.dscatalog.repositories.CategoryRepository;
import com.gabrielsilva.dscatalog.repositories.ProductRepository;
import com.gabrielsilva.dscatalog.services.exceptions.DatabaseException;
import com.gabrielsilva.dscatalog.services.exceptions.ObjectNotFoundException;
import com.gabrielsilva.dscatalog.util.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    public Page<ProductDTO> findAllPaged(String name, String categoryId, Pageable pageable) {

        List<Long> categoryIds = List.of();
        if(!"0".equals(categoryId)) {
            categoryIds = Arrays.stream(categoryId.split(",")).map(Long::parseLong).toList();
        }
        Page<ProductProjection> page = repository.searchProducts(categoryIds, name, pageable);
        List<Long> productIds = page.map(x -> x.getId()).toList();

        List<Product> entities = repository.searchProductsWithCategories(productIds);
        entities = (List<Product>) Utils.replace(page.getContent(), entities);

        List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();

        Page<ProductDTO> pageDto = new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
        return pageDto;
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

            entity = repository.save(entity);

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

package com.gabrielsilva.dscatalog.services;

import com.gabrielsilva.dscatalog.dto.ProductDTO;
import com.gabrielsilva.dscatalog.entities.Category;
import com.gabrielsilva.dscatalog.entities.Product;
import com.gabrielsilva.dscatalog.repositories.CategoryRepository;
import com.gabrielsilva.dscatalog.repositories.ProductRepository;
import com.gabrielsilva.dscatalog.services.exceptions.DatabaseException;
import com.gabrielsilva.dscatalog.services.exceptions.ObjectNotFoundException;
import com.gabrielsilva.dscatalog.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private long categoryId;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
       existingId = 1L;
       nonExistingId = 2L;
       dependentId = 3L;
       categoryId = 1L;
       product = Factory.createProduct();
       category = Factory.createCategory();
       productDTO = Factory.createProductDTO();
       page = new PageImpl<>(List.of(product));
    }

    @Test
    public void findAllPagedShouldReturnPage() {
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExist() {
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));

        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Mockito.verify(repository, Mockito.times(1)).findById(existingId);
    }

    @Test
    public void findByIdShouldThrowObjectNotFoundExceptionWhenIdNotExist() {
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
    }

    @Test
    public void updateShouldReturnProductDTOUpdatedWhenIdExist() {
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(categoryRepository.getReferenceById(categoryId)).thenReturn(category);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        ProductDTO result = service.update(existingId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getId());
        Mockito.verify(repository, Mockito.times(1)).getReferenceById(existingId);
        Mockito.verify(categoryRepository, Mockito.times(1)).getReferenceById(categoryId);
    }

    @Test
    public void updateShouldThrowObjectNotFoundExceptionWhenIdNotExist() {
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            service.update(nonExistingId, productDTO);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }

    @Test
    public void deleteShouldThrowObjectNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);

        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }
}

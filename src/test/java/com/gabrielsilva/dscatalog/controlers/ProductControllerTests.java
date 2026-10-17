package com.gabrielsilva.dscatalog.controlers;

import com.gabrielsilva.dscatalog.controllers.ProductController;
import com.gabrielsilva.dscatalog.dto.ProductDTO;
import com.gabrielsilva.dscatalog.services.ProductService;
import com.gabrielsilva.dscatalog.services.exceptions.ObjectNotFoundException;
import com.gabrielsilva.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    private long existingId;
    private long nonexistingId;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonexistingId = 2L;
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
    }

    @Test
    public void findAllShouldReturnPage () throws Exception {
        Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/products")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        Mockito.when(service.findById(existingId)).thenReturn(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        //Outro tipo de notação com import estático
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdNotExists() throws Exception {
        Mockito.when(service.findById(nonexistingId)).thenThrow(ObjectNotFoundException.class);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonexistingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}

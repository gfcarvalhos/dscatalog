package com.gabrielsilva.dscatalog.controlers;

import com.gabrielsilva.dscatalog.controllers.ProductController;
import com.gabrielsilva.dscatalog.dto.ProductDTO;
import com.gabrielsilva.dscatalog.entities.Category;
import com.gabrielsilva.dscatalog.repositories.CategoryRepository;
import com.gabrielsilva.dscatalog.services.ProductService;
import com.gabrielsilva.dscatalog.services.exceptions.DatabaseException;
import com.gabrielsilva.dscatalog.services.exceptions.ObjectNotFoundException;
import com.gabrielsilva.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;
    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
    }

    @Test
    public void findAllShouldReturnPage () throws Exception {
        Mockito.when(service.findAllPaged(any())).thenReturn(page);

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
        Mockito.when(service.findById(nonExistingId)).thenThrow(ObjectNotFoundException.class);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception {
        Mockito.when(service.update(eq(existingId), any())).thenReturn(productDTO);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
                .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdNotExists() throws Exception {
        Mockito.when(service.update(eq(nonExistingId), any())).thenThrow(ObjectNotFoundException.class);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                        .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    public void insertShouldReturnCreatedWhenANewProductDTOIsSent() throws Exception {
        Mockito.when(service.insert(any())).thenReturn(productDTO);

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.post("/products")
                        .content(jsonBody).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExist() throws Exception {
        Mockito.doNothing().when(service).delete(existingId);

        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void deleteShouldThrowNotFoundWhenIdNotExist() throws Exception {
        Mockito.doThrow(ObjectNotFoundException.class).when(service).delete(nonExistingId);

        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteShouldThrowBadRequestWhenIdNotExist() throws Exception {
        Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);

        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/products/{id}", dependentId)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}

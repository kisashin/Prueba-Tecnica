package com.example.products.controller;

import com.example.products.dto.InventoryDTO;
import com.example.products.dto.JsonApiResponse;
import com.example.products.dto.PurchaseDTO;
import com.example.products.dto.QuantityDTO;
import com.example.products.model.Inventory;
import com.example.products.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RestTemplate restTemplate;

    private final String apiKey = "secret-key-123";

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setQuantity(10);
        repository.save(inventory);

        InventoryDTO mockProduct = new InventoryDTO(1L, 10);
        ResponseEntity<Object> mockResponse = new ResponseEntity<>(mockProduct, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class))).thenReturn(mockResponse);
    }

    @Test
    public void testGetInventory() throws Exception {
        mockMvc.perform(get("/inventory/1")
                .header("X-API-KEY", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("inventory"))
                .andExpect(jsonPath("$.attributes.productId").value(1))
                .andExpect(jsonPath("$.attributes.quantity").value(10));
    }

    @Test
    public void testUpdateQuantity() throws Exception {
        QuantityDTO dto = new QuantityDTO(20);
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(put("/inventory/1")
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes.quantity").value(20));
    }

    @Test
    public void testPurchaseProduct() throws Exception {
        PurchaseDTO dto = new PurchaseDTO(1L, 5);
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post("/inventory/purchase")
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes.quantity").value(5));
    }

    @Test
    public void testCreateInventory() throws Exception {
        // Simulamos que el producto con ID 2 existe en products-service
        JsonApiResponse<InventoryDTO> mockResponse = new JsonApiResponse<>("product", "2", new InventoryDTO(2L, 15));
        ResponseEntity<Object> entity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://products-service:8080/products/2"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class))).thenReturn(entity);

        // JSON para enviar
        InventoryDTO newInventory = new InventoryDTO(2L, 15);
        String json = mapper.writeValueAsString(newInventory);

        // Ejecutar y verificar
        mockMvc.perform(post("/inventory")
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes.productId").value(2))
                .andExpect(jsonPath("$.attributes.quantity").value(15));
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/inventory/1"))
                .andExpect(status().isBadRequest());
    }
}

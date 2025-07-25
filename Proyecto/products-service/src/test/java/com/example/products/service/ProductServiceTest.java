package com.example.products.service;

import com.example.products.dto.ProductDTO;
import com.example.products.dto.JsonApiResponse;
import com.example.products.model.Product;
import com.example.products.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Test
    void testCreateProduct() {
        ProductRepository mockRepo = mock(ProductRepository.class);
        ProductService service = new ProductService(mockRepo);

        Product input = new Product();
        input.setName("Laptop");
        input.setDescription("Electronics");
        input.setPrice(1000.0);

        Product saved = new Product();
        saved.setId(1L);
        saved.setName("Laptop");
        saved.setDescription("Electronics");
        saved.setPrice(1000.0);

        when(mockRepo.save(any(Product.class))).thenReturn(saved);

        ProductDTO dto = new ProductDTO(null, "Laptop", 1000.0, "Electronics", 0);
        JsonApiResponse<ProductDTO> response = service.create(dto);

        assertEquals("product", response.type());
        assertEquals("1", response.id());
        assertEquals("Laptop", response.attributes().name());
        assertEquals("Electronics", response.attributes().description());
        assertEquals(1000.0, response.attributes().price());
    }

    @Test
    void testGetById() {
        ProductRepository mockRepo = mock(ProductRepository.class);

        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Phone");
        existing.setDescription("Smartphone");
        existing.setPrice(500.0);

        when(mockRepo.findById(1L)).thenReturn(Optional.of(existing));

        ProductService service = new ProductService(mockRepo) {
            @Override
            protected int getStockForProduct(Long productId) {
                return 10; // mock del inventario
            }
        };

        JsonApiResponse<ProductDTO> response = service.getById(1L);

        assertEquals("product", response.type());
        assertEquals("1", response.id());
        assertEquals("Phone", response.attributes().name());
        assertEquals("Smartphone", response.attributes().description());
        assertEquals(500.0, response.attributes().price());
        assertEquals(10, response.attributes().quantity());
    }
}

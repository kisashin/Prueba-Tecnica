package com.example.products.service;

import com.example.products.dto.ProductDTO;
import com.example.products.dto.JsonApiResponse;
import com.example.products.dto.InventoryDTO;
import com.example.products.model.Product;
import com.example.products.repository.ProductRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final RestTemplate restTemplate;
    private final String inventoryUrl = "http://localhost:8081/inventory/";
    private static final String API_KEY = "secret-key-123"; // misma clave del otro servicio

    public ProductService(ProductRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    public JsonApiResponse<ProductDTO> create(ProductDTO dto) {
        Product p = new Product();
        p.setName(dto.name());
        p.setPrice(dto.price());
        p.setDescription(dto.description());
        Product saved = repository.save(p);

        ProductDTO response = new ProductDTO(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                saved.getDescription(),
                0 // sin inventario aún
        );

        return new JsonApiResponse<>("product", String.valueOf(saved.getId()), response);
    }

    public JsonApiResponse<ProductDTO> getById(Long id) {
        Product p = repository.findById(id).orElseThrow();
        int quantity = getStockForProduct(p.getId());

        ProductDTO response = new ProductDTO(
                p.getId(),
                p.getName(),
                p.getPrice(),
                p.getDescription(),
                quantity
        );

        return new JsonApiResponse<>("product", String.valueOf(p.getId()), response);
    }

    public List<JsonApiResponse<ProductDTO>> getAll() {
        return repository.findAll().stream()
                .map(p -> {
                    int quantity = getStockForProduct(p.getId());
                    ProductDTO dto = new ProductDTO(
                            p.getId(),
                            p.getName(),
                            p.getPrice(),
                            p.getDescription(),
                            quantity
                    );
                    return new JsonApiResponse<>("product", String.valueOf(p.getId()), dto);
                })
                .toList();
    }

    protected int getStockForProduct(Long productId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", API_KEY);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonApiResponse<InventoryDTO>> response = restTemplate.exchange(
                    inventoryUrl + productId,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<JsonApiResponse<InventoryDTO>>() {}
            );

            JsonApiResponse<InventoryDTO> body = response.getBody();
            if (body != null && body.attributes() != null) {
                return body.attributes().quantity();
            } else {
                return 0;
            }

        } catch (Exception e) {
            System.out.println("Error al consultar inventario: " + e.getMessage());
            return 0;
        }
    }
}

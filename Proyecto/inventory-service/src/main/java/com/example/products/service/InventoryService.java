package com.example.products.service;

import com.example.products.dto.InventoryDTO;
import com.example.products.model.Inventory;
import com.example.products.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository repository;
    private final RestTemplate restTemplate;

    public InventoryService(InventoryRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public InventoryDTO getInventory(Long productId) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("Inventory not found for product ID: " + productId));

        return new InventoryDTO(inventory.getProductId(), inventory.getQuantity());
    }

    public InventoryDTO updateQuantity(Long productId, int quantity) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("Inventory not found for product ID: " + productId));

        inventory.setQuantity(quantity);
        repository.save(inventory);
        return new InventoryDTO(inventory.getProductId(), inventory.getQuantity());
    }

    public InventoryDTO createInventory(Long productId, int quantity) {
        Optional<Inventory> existing = repository.findByProductId(productId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Inventory for product already exists");
        }

        // Validar que el producto exista en products-service
        String productUrl = "http://products-service:8080/products/" + productId;
        try {
            restTemplate.getForObject(productUrl, Object.class);
        } catch (Exception e) {
            throw new NoSuchElementException("Product not found in products-service");
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);
        repository.save(inventory);

        return new InventoryDTO(inventory.getProductId(), inventory.getQuantity());
    }

    public InventoryDTO purchaseProduct(Long productId, int quantity) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new NoSuchElementException("No inventory found for product ID: " + productId));

        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough inventory for product ID: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        repository.save(inventory);

        return new InventoryDTO(inventory.getProductId(), inventory.getQuantity());
    }
}

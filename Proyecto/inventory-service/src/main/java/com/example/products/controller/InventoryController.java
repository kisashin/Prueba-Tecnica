package com.example.products.controller;

import com.example.products.dto.InventoryDTO;
import com.example.products.dto.JsonApiResponse;
import com.example.products.dto.QuantityDTO;
import com.example.products.dto.PurchaseDTO;
import com.example.products.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;
    private static final String VALID_API_KEY = "secret-key-123";

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getInventory(
            @RequestHeader("X-API-KEY") String apiKey,
            @PathVariable Long productId) {

        if (!VALID_API_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key");
        }

        InventoryDTO dto = service.getInventory(productId);
        return ResponseEntity.ok(new JsonApiResponse<>("inventory", String.valueOf(dto.productId()), dto));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateQuantity(
            @RequestHeader("X-API-KEY") String apiKey,
            @PathVariable Long productId,
            @RequestBody QuantityDTO quantityDTO) {

        if (!VALID_API_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key");
        }

        InventoryDTO dto = service.updateQuantity(productId, quantityDTO.quantity());
        return ResponseEntity.ok(new JsonApiResponse<>("inventory", String.valueOf(dto.productId()), dto));
    }

    @PostMapping
    public ResponseEntity<?> createInventory(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestBody InventoryDTO dto) {

        if (!VALID_API_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key");
        }

        InventoryDTO result = service.createInventory(dto.productId(), dto.quantity());
        return ResponseEntity.ok(new JsonApiResponse<>("inventory", String.valueOf(result.productId()), result));
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseProduct(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestBody PurchaseDTO purchaseDTO) {

        if (!VALID_API_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key");
        }

        InventoryDTO dto = service.purchaseProduct(purchaseDTO.productId(), purchaseDTO.quantity());
        return ResponseEntity.ok(new JsonApiResponse<>("inventory", String.valueOf(dto.productId()), dto));
    }
}

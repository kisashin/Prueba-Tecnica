package com.example.products.controller;

import com.example.products.dto.JsonApiResponse;
import com.example.products.dto.ProductDTO;
import com.example.products.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<JsonApiResponse<ProductDTO>> create(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonApiResponse<ProductDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<JsonApiResponse<ProductDTO>>> list() {
        return ResponseEntity.ok(service.getAll());
    }
}

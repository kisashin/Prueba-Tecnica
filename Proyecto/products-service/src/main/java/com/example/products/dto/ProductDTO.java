package com.example.products.dto;

public record ProductDTO(
    Long id,
    String name,
    double price,
    String description,
    int quantity
) {}

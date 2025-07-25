package com.example.products.dto;

public record JsonApiResponse<T>(String type, String id, T attributes) {}

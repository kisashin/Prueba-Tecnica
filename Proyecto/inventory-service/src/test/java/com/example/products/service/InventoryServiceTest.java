package com.example.products.service;

import com.example.products.dto.QuantityDTO;
import com.example.products.model.Inventory;
import com.example.products.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InventoryServiceTest {

    private InventoryRepository inventoryRepository;
    private InventoryService inventoryService;

    @BeforeEach
    public void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
        RestTemplate restTemplate = mock(RestTemplate.class);  
        inventoryService = new InventoryService(inventoryRepository, restTemplate); 
    }

    @Test
    public void testGetInventorySuccess() {
        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setQuantity(10);

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        var result = inventoryService.getInventory(1L);

        assertEquals(1L, result.productId());
        assertEquals(10, result.quantity());
    }

    @Test
    public void testUpdateQuantity_WhenInventoryExists() {
        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setQuantity(5);

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        var result = inventoryService.updateQuantity(1L, 20);

        assertEquals(1L, result.productId());
        assertEquals(20, result.quantity());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    public void testPurchaseProduct_Successful() {
        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setQuantity(10);

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        var result = inventoryService.purchaseProduct(1L, 3);

        assertEquals(7, result.quantity());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    public void testPurchaseProduct_InsufficientStock() {
        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setQuantity(2);

        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> inventoryService.purchaseProduct(1L, 5));

        assertEquals("Not enough inventory for product ID: 1", ex.getMessage());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    public void testGetInventory_NotFound() {
        when(inventoryRepository.findByProductId(99L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(NoSuchElementException.class, () -> inventoryService.getInventory(99L));

        assertTrue(ex.getMessage().contains("Inventory not found"));
    }
}

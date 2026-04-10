package com.example.minishop.service;

import com.example.minishop.entity.Product;
import com.example.minishop.repository.MockDataStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final MockDataStore mockDataStore;

    public ProductService(MockDataStore mockDataStore) {
        this.mockDataStore = mockDataStore;
    }

    public List<Product> getAllProducts() {
        return mockDataStore.findAllProducts();
    }
}

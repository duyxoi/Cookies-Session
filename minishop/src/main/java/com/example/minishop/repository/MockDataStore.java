// src/main/java/com/example/minishop/repository/MockDataStore.java
package com.example.minishop.repository;

import com.example.minishop.entity.Product;
import com.example.minishop.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MockDataStore {

    // Thêm danh sách User giả định
    private static final List<User> USERS = List.of(
            new User(1, "admin", "admin123"),
            new User(2, "duy", "123456")
    );

    // Hàm kiểm tra đăng nhập
    public boolean authenticate(String username, String password) {
        return USERS.stream()
                .anyMatch(user -> user.username().equals(username) && user.password().equals(password));
    }

    // Danh sách sản phẩm tĩnh (Thay cho bảng Product trong CSDL)
    private static final List<Product> PRODUCTS = List.of(
            new Product(1, "iPhone 15 Pro", 25000000),
            new Product(2, "MacBook Pro M3", 35000000),
            new Product(3, "AirPods Pro 2", 5000000)
    );

    public List<Product> findAllProducts() {
        return PRODUCTS;
    }

    public Optional<Product> findProductById(int id) {
        return PRODUCTS.stream()
                .filter(p -> p.id() == id)
                .findFirst();
    }
}
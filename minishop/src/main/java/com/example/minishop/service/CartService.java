package com.example.minishop.service;

import com.example.minishop.entity.CartItem;
import com.example.minishop.entity.Product;
import com.example.minishop.repository.MockDataStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final MockDataStore mockDataStore;

    public CartService(MockDataStore mockDataStore) {
        this.mockDataStore = mockDataStore;
    }

    public void addProductToCart(int productId, List<CartItem> cart) {
        // 1. Tìm sản phẩm trong "CSDL"
        Product product = mockDataStore.findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));

        // 2. Kiểm tra xem sản phẩm đã có trong giỏ chưa (Cộng dồn số lượng)
        Optional<CartItem> existingItem = cart.stream()
                .filter(item -> item.product() == product)
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem oldItem = existingItem.get();
            cart.remove(oldItem);
            cart.add(new CartItem(oldItem.product(), oldItem.quantity() + 1));
        } else {
            // 3. Thêm mới nếu chưa có
            cart.add(new CartItem(product, 1));
        }
    }
}
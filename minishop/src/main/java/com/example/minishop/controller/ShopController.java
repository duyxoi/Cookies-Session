// src/main/java/com/example/minishop/controller/ShopController.java
package com.example.minishop.controller;

import com.example.minishop.dto.LoginRequest;
import com.example.minishop.entity.CartItem;
import com.example.minishop.exception.UnauthorizedException;
import com.example.minishop.repository.MockDataStore;
import com.example.minishop.service.CartService;
import com.example.minishop.service.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShopController {

    private final ProductService productService;
    private final CartService cartService;

    // Dependency Injection


    private final MockDataStore mockDataStore;

    public ShopController(ProductService productService, CartService cartService, MockDataStore mockDataStore) {
        this.productService = productService;
        this.cartService = cartService;
        this.mockDataStore = mockDataStore;
    }

    // 1. TRANG CHỦ & XEM SẢN PHẨM
    @GetMapping("/home")
    public Map<String, Object> getHome(
            @CookieValue(value = "theme", defaultValue = "light") String theme,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        String message = (username != null) ? "Xin chào, " + username : "Bạn chưa đăng nhập";

        return Map.of(
                "title", "Mini Profile App",
                "message", message,
                "theme", theme,
                "products", productService.getAllProducts()
        );
    }

    // 2. THIẾT LẬP THEME
    @GetMapping("/set-theme/{theme}")
    public ResponseEntity<Map<String, String>> setTheme(@PathVariable String theme, HttpServletResponse response) {
        if ("light".equals(theme) || "dark".equals(theme)) {
            Cookie cookie = new Cookie("theme", theme);
            cookie.setMaxAge(10 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.ok(Map.of("message", "Đã đổi giao diện sang: " + theme));
        }
        throw new IllegalArgumentException("Theme chỉ được là 'light' hoặc 'dark'");
    }

    // 3. ĐĂNG NHẬP
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest req, HttpSession session) {
        // Kiểm tra đầu vào có bị trống không
        if (req.username() == null || req.password() == null || req.username().isBlank() || req.password().isBlank()) {
            throw new IllegalArgumentException("Username và Password không được để trống");
        }

        // Kiểm tra xác thực qua "CSDL" giả
        boolean isValidUser = mockDataStore.authenticate(req.username(), req.password());

        if (!isValidUser) {
            throw new UnauthorizedException("Sai tên đăng nhập hoặc mật khẩu");
        }

        // Nếu đúng mật khẩu thì mới cấp Session
        session.setAttribute("username", req.username());
        session.setAttribute("cart", new ArrayList<CartItem>());

        return ResponseEntity.ok(Map.of("message", "Đăng nhập thành công"));
    }

    // 4. THÊM VÀO GIỎ HÀNG
    @PostMapping("/cart/add/{productId}")
    public ResponseEntity<Map<String, String>> addToCart(@PathVariable int productId, HttpSession session) {
        // Kiểm tra Auth bằng Exception
        if (session.getAttribute("username") == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để mua hàng");
        }

        // Lấy giỏ hàng từ session và gọi Service xử lý
        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        cartService.addProductToCart(productId, cart);
        session.setAttribute("cart", cart); // Cập nhật lại session

        return ResponseEntity.ok(Map.of("message", "Đã cập nhật giỏ hàng"));
    }

    // 5. XEM GIỎ HÀNG
    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> viewCart(HttpSession session) {
        if (session.getAttribute("username") == null) {
            throw new UnauthorizedException("Vui lòng đăng nhập để xem giỏ hàng");
        }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        return ResponseEntity.ok(cart);
    }

    // 6. ĐĂNG XUẤT
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }
}
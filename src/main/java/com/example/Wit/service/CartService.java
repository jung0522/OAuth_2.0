package com.example.Wit.service;

import com.example.Wit.dto.CartResponseDto;
import com.example.Wit.entity.Cart;
import com.example.Wit.entity.Product;
import com.example.Wit.entity.User;
import com.example.Wit.repository.CartRepository;
import com.example.Wit.repository.ProductRepository;
import com.example.Wit.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // 장바구니에 제품 추가
    public void addProductToCart(String email, Long productId, int quantity) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = new Cart(user, product, quantity);
        cartRepository.save(cart);
    }

    // 사용자의 장바구니 조회
    public List<CartResponseDto> getCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Cart> cartItems = cartRepository.findByUser(user);

        return cartItems.stream()
                .map(cart -> new CartResponseDto(
                        cart.getProduct().getName(),
                        cart.getProduct().getPrice(),
                        cart.getProduct().getImageUrl(),
                        cart.getQuantity()
                ))
                .collect(Collectors.toList());
    }
}

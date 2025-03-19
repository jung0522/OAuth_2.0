package com.example.Wit.controller;

import com.example.Wit.dto.CartRequestDto;
import com.example.Wit.dto.CartResponseDto;
import com.example.Wit.service.CartService;
import com.example.Wit.service.KakaoOAuth2Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final KakaoOAuth2Service kakaoOAuth2Service;

    public CartController(CartService cartService, KakaoOAuth2Service kakaoOAuth2Service) {
        this.cartService = cartService;
        this.kakaoOAuth2Service = kakaoOAuth2Service;
    }

    // 장바구니에 제품 추가
    @PostMapping("/{productId}")
    public ResponseEntity<String> addProductToCart(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @PathVariable Long productId,
            @RequestBody CartRequestDto cartRequestDto) {

        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token format");
        }

        String accessToken = token.substring(7);
        String userEmail = kakaoOAuth2Service.getKakaoUserInfo(accessToken).getEmail();

        cartService.addProductToCart(userEmail, productId, cartRequestDto.getQuantity());

        return ResponseEntity.ok("Product added to cart successfully");
    }

    // 장바구니 조회
    @GetMapping
    public ResponseEntity<List<CartResponseDto>> getCart(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (!token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(null);
        }

        String accessToken = token.substring(7);
        String userEmail = kakaoOAuth2Service.getKakaoUserInfo(accessToken).getEmail();

        List<CartResponseDto> cartItems = cartService.getCart(userEmail);
        return ResponseEntity.ok(cartItems);
    }
}

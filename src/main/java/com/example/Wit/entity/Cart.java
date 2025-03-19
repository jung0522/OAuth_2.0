package com.example.Wit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String productName;  // 제품 이름 필드 추가

    @Column(nullable = false)
    private double productPrice;  // 제품 가격 필드 추가

    @Column
    private String productImageUrl;  // 제품 이미지 URL 필드 추가

    public Cart(User user, Product product, int quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.productName = product.getName();  // 제품 이름 설정
        this.productPrice = product.getPrice();  // 제품 가격 설정
        this.productImageUrl = product.getImageUrl();  // 제품 이미지 URL 설정
    }
}

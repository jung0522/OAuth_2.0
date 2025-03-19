package com.example.Wit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartResponseDto {
    private String productName;
    private double productPrice;
    private String productImageUrl;
    private int quantity;
}

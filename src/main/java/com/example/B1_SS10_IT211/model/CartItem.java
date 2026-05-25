package com.example.B1_SS10_IT211.model;

import lombok.Data;

@Data
public class CartItem {
    private Long id;
    private String userId;
    private String productId;
    private Integer quantity;
}
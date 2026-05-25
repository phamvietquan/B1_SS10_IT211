package com.example.session10_ex1.model;

import lombok.Data;

@Data
public class CartItem {
    private Long id;
    private String userId;
    private String productId;
    private Integer quantity;
}
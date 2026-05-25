package com.example.session10_ex1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemDTO {

    @NotBlank(message = "User ID không được để trống")
    private String userId;

    @NotBlank(message = "Product ID không được để trống")
    private String productId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng sản phẩm thêm vào giỏ phải lớn hơn 0")
    private Integer quantity;
}
package com.example.session10_ex1.controller;

import com.example.session10_ex1.dto.CartItemDTO;
import com.example.session10_ex1.model.CartItem;
import com.example.session10_ex1.service.CartService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@Slf4j
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody CartItemDTO cartItemDTO, BindingResult bindingResult) {
        log.info("Nhận request POST thêm giỏ hàng. Dữ liệu: {}", cartItemDTO);

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                if ("productId".equals(error.getField()) && (cartItemDTO.getProductId() == null || cartItemDTO.getProductId().trim().isEmpty())) {
                    log.warn("Phát hiện hành vi bất thường: productId gửi lên bị trống hoặc chuỗi rỗng!");
                }
                if ("quantity".equals(error.getField()) && (cartItemDTO.getQuantity() != null && cartItemDTO.getQuantity() <= 0)) {
                    log.warn("Phát hiện hành vi bất thường: quantity gửi lên <= 0 (giá trị: {})!", cartItemDTO.getQuantity());
                }
            });

            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.joining(", "));

            log.warn("Request thêm giỏ hàng bị từ chối do vi phạm Validation: {}", errorMsg);
            return ResponseEntity.badRequest().body(errorMsg);
        }

        try {
            CartItem result = cartService.addToCart(cartItemDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("Lỗi hệ thống bất ngờ phát sinh khi xử lý thêm giỏ hàng: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xử lý hệ thống");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCartByUserId(@PathVariable String userId) {
        log.info("Nhận request GET lấy giỏ hàng cho User ID: {}", userId);
        List<CartItem> items = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCartItems());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCart(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            CartItem updated = cartService.updateCartItemQuantity(id, quantity);
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy mục giỏ hàng");
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable Long id) {
        boolean deleted = cartService.deleteCartItem(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy mục giỏ hàng để xóa");
        }
        return ResponseEntity.ok("Xóa thành công");
    }
}
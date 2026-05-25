package com.example.session10_ex1.service;

import com.example.session10_ex1.dto.CartItemDTO;
import com.example.session10_ex1.model.CartItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartService {

    private final ConcurrentHashMap<Long, CartItem> cartStorage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public CartItem addToCart(CartItemDTO dto) {
        log.info("Xử lý nghiệp vụ thêm giỏ hàng cho User: {}, Sản phẩm: {}, Số lượng: {}",
                dto.getUserId(), dto.getProductId(), dto.getQuantity());

        synchronized (cartStorage) {
            for (CartItem item : cartStorage.values()) {
                if (item.getUserId().equals(dto.getUserId()) && item.getProductId().equals(dto.getProductId())) {
                    int oldQty = item.getQuantity();
                    item.setQuantity(oldQty + dto.getQuantity());
                    log.info("Cập nhật cộng dồn số lượng thành công cho User: {}, Sản phẩm: {}. Số lượng cũ: {}, Số lượng mới: {}",
                            item.getUserId(), item.getProductId(), oldQty, item.getQuantity());
                    return item;
                }
            }

            CartItem newItem = new CartItem();
            newItem.setId(idGenerator.getAndIncrement());
            newItem.setUserId(dto.getUserId());
            newItem.setProductId(dto.getProductId());
            newItem.setQuantity(dto.getQuantity());

            cartStorage.put(newItem.getId(), newItem);
            log.info("Tạo mới thành công bản ghi giỏ hàng ID: {} cho User: {}", newItem.getId(), newItem.getUserId());
            return newItem;
        }
    }

    public List<CartItem> getCartByUserId(String userId) {
        log.info("Truy xuất danh sách giỏ hàng của User: {}", userId);
        return cartStorage.values().stream()
                .filter(item -> item.getUserId().equalsIgnoreCase(userId))
                .collect(Collectors.toList());
    }

    public List<CartItem> getAllCartItems() {
        return new ArrayList<>(cartStorage.values());
    }

    public CartItem updateCartItemQuantity(Long id, Integer quantity) {
        if (quantity <= 0) {
            log.warn("Yêu cầu cập nhật số lượng không hợp lệ cho ID: {}, Số lượng gửi lên: {}", id, quantity);
            throw new IllegalArgumentException("Số lượng cập nhật phải lớn hơn 0");
        }
        CartItem item = cartStorage.get(id);
        if (item != null) {
            item.setQuantity(quantity);
            log.info("Cập nhật số lượng thủ công thành công cho bản ghi ID: {}, Số lượng mới: {}", id, quantity);
            return item;
        }
        log.warn("Không tìm thấy bản ghi giỏ hàng với ID: {} để cập nhật", id);
        return null;
    }

    public boolean deleteCartItem(Long id) {
        if (cartStorage.containsKey(id)) {
            cartStorage.remove(id);
            log.info("Xóa thành công bản ghi giỏ hàng ID: {}", id);
            return true;
        }
        log.warn("Thao tác xóa thất bại, không tìm thấy bản ghi giỏ hàng ID: {}", id);
        return false;
    }
}
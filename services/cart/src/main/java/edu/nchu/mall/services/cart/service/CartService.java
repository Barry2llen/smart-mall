package edu.nchu.mall.services.cart.service;

import edu.nchu.mall.models.vo.CartItemVO;
import edu.nchu.mall.services.cart.dto.CartItemDTO;
import edu.nchu.mall.models.vo.Cart;
import lombok.Getter;

import java.util.List;

public interface CartService {
    List<CartItemVO> getCartItems(Long userId);

    Cart getCart(Long userId);

    Status addCartItem(Long userId, CartItemDTO dto);

    Status deleteCartItem(Long userId, Long skuId);

    Status checkCartItem(Long userId, Long skuId);

    Status uncheckCartItem(Long userId, Long skuId);

    Status updateCartItemCount(Long userId, Long skuId, Integer count);

    @Getter
    enum Status {
        SUCCESS,
        CART_FULL("购物车已满"),
        SKU_NOT_FOUND("商品不存在"),
        SKU_NOT_ENOUGH("商品库存不足"),
        CART_ITEM_NOT_FOUND("购物项不存在"),
        ERROR;

        private final String message;

        Status() {
            this.message = name();
        }

        Status(String message) {
            this.message = message;
        }

    }
}

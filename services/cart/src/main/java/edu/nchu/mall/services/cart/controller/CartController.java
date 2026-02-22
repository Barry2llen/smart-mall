package edu.nchu.mall.services.cart.controller;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.services.cart.dto.CartItemDTO;
import edu.nchu.mall.services.cart.service.CartService;
import edu.nchu.mall.services.cart.vo.Cart;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "购物车服务")
@RestController
public class CartController {
    @Autowired
    CartService cartService;

    @Parameters(@Parameter(name = "userId", description = "用户Id"))
    @Operation(description = "获取购物车")
    @GetMapping
    public Cart getCart(@RequestHeader("X-User-Id") Long userId) {
        return cartService.getCart(userId);
    }

    @Parameters({
            @Parameter(name = "userId", description = "用户Id"),
            @Parameter(name = "dto", description = "购物车项")
    })
    @Operation(description = "添加购物车项")
    @PostMapping
    public R<?> addCartItem(@RequestHeader("X-User-Id") Long userId, @RequestBody @Valid CartItemDTO dto) {
        CartService.Status res = cartService.addCartItem(userId, dto);
        return res == CartService.Status.SUCCESS ? R.success() : R.fail(res.getMessage());
    }

    @Parameters({
            @Parameter(name = "userId", description = "用户Id"),
            @Parameter(name = "skuId", description = "商品skuId")
    })
    @Operation(description = "删除购物车项")
    @DeleteMapping("/{skuId}")
    public R<?> deleteCartItem(@RequestHeader("X-User-Id") Long userId, @PathVariable Long skuId) {
        CartService.Status res = cartService.deleteCartItem(userId, skuId);
        return res == CartService.Status.SUCCESS ? R.success() : R.fail(res.getMessage());
    }

    @Parameters({
            @Parameter(name = "userId", description = "用户Id"),
            @Parameter(name = "skuId", description = "商品skuId")
    })
    @Operation(description = "选中购物车项")
    @PutMapping("/{skuId}/check")
    public R<?> checkCartItem(@RequestHeader("X-User-Id") Long userId, @PathVariable Long skuId) {
        CartService.Status res = cartService.checkCartItem(userId, skuId);
        return res == CartService.Status.SUCCESS ? R.success() : R.fail(res.getMessage());
    }

    @Parameters({
            @Parameter(name = "userId", description = "用户Id"),
            @Parameter(name = "skuId", description = "商品skuId")
    })
    @Operation(description = "取消选中购物车项")
    @PutMapping("/{skuId}/uncheck")
    public R<?> uncheckCartItem(@RequestHeader("X-User-Id") Long userId, @PathVariable Long skuId) {
        CartService.Status res = cartService.uncheckCartItem(userId, skuId);
        return res == CartService.Status.SUCCESS ? R.success() : R.fail(res.getMessage());
    }

    @Parameters({
            @Parameter(name = "userId", description = "用户Id"),
            @Parameter(name = "skuId", description = "商品skuId"),
            @Parameter(name = "count", description = "商品数量，必须大于0")
    })
    @Operation(description = "修改购物车项数量")
    @PutMapping("/{skuId}/count")
    public R<?> updateCartItemCount(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long skuId,
                                    @RequestParam @Positive(message = "数量必须大于0") Integer count) {
        CartService.Status res = cartService.updateCartItemCount(userId, skuId, count);
        return res == CartService.Status.SUCCESS ? R.success() : R.fail(res.getMessage());
    }
}

package edu.nchu.mall.services.cart.controller;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.vo.CartItemVO;
import edu.nchu.mall.services.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/{id}")
    public R<List<CartItemVO>> getRefreshedCartItems(@PathVariable Long id) {
        return R.success(cartService.getCartItems(id));
    }
}

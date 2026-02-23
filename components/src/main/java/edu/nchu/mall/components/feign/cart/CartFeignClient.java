package edu.nchu.mall.components.feign.cart;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.vo.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("cart")
public interface CartFeignClient {
    @GetMapping("/cart/{id}")
    R<Cart> getRefreshedCartItems(@PathVariable Long id);
}

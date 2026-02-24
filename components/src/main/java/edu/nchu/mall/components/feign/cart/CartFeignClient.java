package edu.nchu.mall.components.feign.cart;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.vo.CartItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("cart")
public interface CartFeignClient {
    @GetMapping("/cart/{id}")
    R<List<CartItemVO>> getRefreshedCartItems(@PathVariable Long id);
}

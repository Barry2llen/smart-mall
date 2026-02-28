package edu.nchu.mall.components.feign.order;

import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("order")
public interface OrderFeignClient {
    @GetMapping("/orders/{id}")
    R<Order> getOrderById(@PathVariable Long id);

    @GetMapping("/orders/sn/{sn}")
    R<Order> getOrderBySn(@PathVariable String sn);
}

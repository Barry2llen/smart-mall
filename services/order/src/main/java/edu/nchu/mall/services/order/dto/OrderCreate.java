package edu.nchu.mall.services.order.dto;

import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "订单创建参数")
public class OrderCreate {
    @Schema(description = "订单信息")
    private Order order;
    @Schema(description = "订单项信息")
    private List<OrderItem> items;
}

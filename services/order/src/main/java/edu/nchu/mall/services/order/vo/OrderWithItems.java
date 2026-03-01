package edu.nchu.mall.services.order.vo;

import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "订单及其订单项")
public class OrderWithItems {

    @Schema(description = "订单信息")
    private Order order;

    @Schema(description = "订单项列表")
    private List<OrderItem> items;
}

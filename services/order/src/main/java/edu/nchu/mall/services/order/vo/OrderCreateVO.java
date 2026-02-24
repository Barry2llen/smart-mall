package edu.nchu.mall.services.order.vo;

import edu.nchu.mall.models.entity.Order;
import edu.nchu.mall.models.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "订单创建参数")
public class OrderCreateVO {
    @Schema(description = "订单信息")
    private Order order;
    @Schema(description = "订单项信息")
    private List<OrderItem> items;
    @Schema(description = "订单应付价格")
    private BigDecimal price;
}

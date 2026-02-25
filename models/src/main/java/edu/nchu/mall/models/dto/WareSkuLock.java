package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.annotation.validation.NotNullCollection;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.models.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "锁定库存")
public class WareSkuLock {
    @NotNull
    @Schema(description = "订单号")
    private String orderSn;
    @NotNullCollection
    @Schema(description = "锁定库存信息")
    private List<OrderLockItemVO> items;
    @NotNull
    @Schema(description = "收货地址")
    private MemberReceiveAddress address;

    public static OrderLockItemVO fromOrderItem(OrderItem item) {
        OrderLockItemVO vo = new OrderLockItemVO();
        vo.setSkuId(item.getSkuId());
        vo.setNum(item.getSkuQuantity());
        return vo;
    }

    @Data
    public static class OrderLockItemVO {
        @NotNull
        @Schema(description = "skuId")
        private Long skuId;
        @NotNull
        @Min(value = 1, message = "下单数量必须大于或等于1")
        @Schema(description = "锁定数量")
        private Integer num;
    }
}

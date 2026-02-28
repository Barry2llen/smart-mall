package edu.nchu.mall.models.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "订单状态")
public enum OrderStatus implements IEnum<Integer> {
    // 【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】
    @Schema(description = "待付款")
    UNPAID(0),
    @Schema(description = "待发货")
    UNSHIPPED(1),
    @Schema(description = "已发货")
    SHIPPED(2),
    @Schema(description = "已完成")
    COMPLETED(3),
    @Schema(description = "已关闭")
    CLOSED(4),
    @Schema(description = "无效订单")
    INVALID(5);

    private final int value;
    OrderStatus(int value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public Integer getValue() {
        return value;
    }
}

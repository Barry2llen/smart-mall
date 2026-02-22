package edu.nchu.mall.services.cart.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "购物项")
public class CartItem {
    @Schema(description = "skuId")
    private Long skuId;
    @Schema(description = "是否选中")
    private Boolean selected = Boolean.TRUE;
    @Schema(description = "数量")
    private Integer count;
    @Schema(description = "创建时间")
    private LocalDateTime time;
}

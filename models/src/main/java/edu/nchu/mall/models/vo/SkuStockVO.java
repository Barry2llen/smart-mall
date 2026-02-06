package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "sku库存视图")
public class SkuStockVO {
    @Schema(description = "skuId")
    private Long skuId;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "锁定库存")
    private Integer stockLocked;
}

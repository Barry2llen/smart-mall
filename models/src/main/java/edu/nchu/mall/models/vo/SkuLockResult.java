package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "sku锁定结果")
public class SkuLockResult {
    @Schema(description = "skuId")
    private Long skuId;
    @Schema(description = "锁定数量")
    private Integer num;
    @Schema(description = "锁定结果")
    private Boolean locked;
}

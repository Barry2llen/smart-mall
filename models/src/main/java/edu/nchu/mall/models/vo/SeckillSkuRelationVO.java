package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "秒杀活动商品关联")
public class SeckillSkuRelationVO {
    
    @Schema(description = "id")
    private Long id;

    
    @Schema(description = "活动id")
    private Long promotionId;

    
    @Schema(description = "商品id")
    private Long skuId;

    
    @Schema(description = "秒杀价格")
    private BigDecimal seckillPrice;

    
    @Schema(description = "秒杀总量")
    private Integer seckillCount;

    
    @Schema(description = "每人限购数量")
    private Integer seckillLimit;

    
    @Schema(description = "排序")
    private Integer seckillSort;
}

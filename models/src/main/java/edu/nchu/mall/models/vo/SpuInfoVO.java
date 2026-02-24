package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "spu信息视图")
public class SpuInfoVO {
    @Schema(description = "spuId")
    private Long spuId;
    @Schema(description = "spu名")
    private String spuName;
    @Schema(description = "分类id")
    private Long catalogId;
    @Schema(description = "分类名")
    private String catalogName;
    @Schema(description = "品牌id")
    private Long brandId;
    @Schema(description = "品牌名")
    private String brandName;
}

package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "销售属性")
public class SkuItemSaleAttrVO {
    @Schema(description = "属性id")
    private Long attrId;
    @Schema(description = "属性名")
    private String attrName;
    @Schema(description = "所有可能的属性值")
    private String attrValues;
}

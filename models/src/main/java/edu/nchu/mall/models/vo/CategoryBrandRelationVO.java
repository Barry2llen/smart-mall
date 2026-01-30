package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分类品牌关联视图对象")
public class CategoryBrandRelationVO {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "分类id")
    private Long catelogId;

    @Schema(description = "分类名称")
    private String catelogName;

    @Schema(description = "品牌id")
    private Long brandId;

    @Schema(description = "品牌名称")
    private String brandName;
}

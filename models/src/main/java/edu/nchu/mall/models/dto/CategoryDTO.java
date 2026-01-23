package edu.nchu.mall.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @Schema(description = "分类id")
    private Long catId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父分类id")
    private Long parentCid;

    @Schema(description = "层级")
    private Integer catLevel;

    @Schema(description = "是否显示[0-不显示，1显示]")
    private Integer showStatus;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "图标地址")
    private String icon;

    @Schema(description = "计量单位")
    private String productUnit;

    @Schema(description = "商品数量")
    private Integer productCount;
}

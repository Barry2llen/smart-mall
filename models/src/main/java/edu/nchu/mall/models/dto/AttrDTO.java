package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "新增或修改商品属性")
public class AttrDTO {

    @Schema(description = "属性id")
    @NotNull(groups = Groups.Update.class, message = "修改商品属性时属性ID不能为空")
    @Null(groups = Groups.Create.class, message = "新增商品属性时属性ID必须为空")
    private Long attrId;

    @Schema(description = "属性名")
    private String attrName;

    @Schema(description = "是否需要检索[0-不需要，1-需要]")
    private Integer searchType;

    @Schema(description = "值类型[0-为单个值，1-可以选择多个值]")
    private Integer valueType;

    @Schema(description = "属性图标")
    private String icon;

    @Schema(description = "可选值列表[用逗号分隔]")
    private String valueSelect;

    @Schema(description = "属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]")
    private Integer attrType;

    @Schema(description = "启用状态[0 - 禁用，1 - 启用]")
    private Long enable;

    @Schema(description = "所属分类")
    private Long catelogId;

    @Schema(description = "所属分组id")
    private Long attrGroupId;

    @Schema(description = "快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整")
    private Integer showDesc;

    @Schema(description = "属性组内排序")
    private Integer attrSort;
}

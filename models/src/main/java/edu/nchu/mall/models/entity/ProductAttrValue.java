package edu.nchu.mall.models.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("pms_product_attr_value")
@Schema(description = "spu属性值")
public class ProductAttrValue {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("spu_id")
    @Schema(description = "商品id")
    private Long spuId;

    @TableField("attr_id")
    @Schema(description = "属性id")
    private Long attrId;

    @TableField("attr_name")
    @Schema(description = "属性名")
    private String attrName;

    @TableField("attr_value")
    @Schema(description = "属性值")
    private String attrValue;

    @TableField("attr_sort")
    @Schema(description = "顺序")
    private Integer attrSort;

    @TableField("quick_show")
    @Schema(description = "快速展示【是否展示在介绍上；0-否 1-是】")
    private Integer quickShow;
}

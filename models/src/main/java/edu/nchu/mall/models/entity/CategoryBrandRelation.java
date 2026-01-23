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
@TableName("pms_category_brand_relation")
@Schema(description = "品牌分类关联")
public class CategoryBrandRelation {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("brand_id")
    @Schema(description = "品牌id")
    private Long brandId;

    @TableField("catelog_id")
    @Schema(description = "分类id")
    private Long catelogId;

    @TableField("brand_name")
    @Schema(description = "brand_name")
    private String brandName;

    @TableField("catelog_name")
    @Schema(description = "catelog_name")
    private String catelogName;
}

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
@TableName("pms_category")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("category")
@Schema(description = "商品三级分类")
public class Category {

    @TableId(value = "cat_id", type = IdType.ASSIGN_ID)
    @Schema(description = "分类id")
    private Long catId;

    @TableField("name")
    @Schema(description = "分类名称")
    private String name;

    @TableField("parent_cid")
    @Schema(description = "父分类id")
    private Long parentCid;

    @TableField("cat_level")
    @Schema(description = "层级")
    private Integer catLevel;

    @TableField("show_status")
    @Schema(description = "是否显示[0-不显示，1显示]")
    private Integer showStatus;

    @TableField("sort")
    @Schema(description = "排序")
    private Integer sort;

    @TableField("icon")
    @Schema(description = "图标地址")
    private String icon;

    @TableField("product_unit")
    @Schema(description = "计量单位")
    private String productUnit;

    @TableField("product_count")
    @Schema(description = "商品数量")
    private Integer productCount;
}

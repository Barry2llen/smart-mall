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
@TableName("pms_attr")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("attr")
    @Schema(description = "商品属性")
public class Attr {

    @TableId(value = "attr_id", type = IdType.ASSIGN_ID)
    @Schema(description = "属性id")
    private Long attrId;

    @TableField("attr_name")
    @Schema(description = "属性名")
    private String attrName;

    @TableField("search_type")
    @Schema(description = "是否需要检索[0-不需要，1-需要]")
    private Integer searchType;

    @TableField("value_type")
    @Schema(description = "值类型[0-为单个值，1-可以选择多个值]")
    private Integer valueType;

    @TableField("icon")
    @Schema(description = "属性图标")
    private String icon;

    @TableField("value_select")
    @Schema(description = "可选值列表[用逗号分隔]")
    private String valueSelect;

    @TableField("attr_type")
    @Schema(description = "属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]")
    private Integer attrType;

    @TableField("enable")
    @Schema(description = "启用状态[0 - 禁用，1 - 启用]")
    private Long enable;

    @TableField("catelog_id")
    @Schema(description = "所属分类")
    private Long catelogId;

    @TableField("show_desc")
    @Schema(description = "快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整")
    private Integer showDesc;
}

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
@TableName("pms_brand")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("brand")
    @Schema(description = "品牌")
public class Brand {

    @TableId(value = "brand_id", type = IdType.ASSIGN_ID)
    @Schema(description = "品牌id")
    private Long brandId;

    @TableField("name")
    @Schema(description = "品牌名")
    private String name;

    @TableField("logo")
    @Schema(description = "品牌logo地址")
    private String logo;

    @TableField("descript")
    @Schema(description = "介绍")
    private String descript;

    @TableField("show_status")
    @Schema(description = "显示状态[0-不显示；1-显示]")
    private Integer showStatus;

    @TableField("first_letter")
    @Schema(description = "检索首字母")
    private String firstLetter;

    @TableField("sort")
    @Schema(description = "排序")
    private Integer sort;
}

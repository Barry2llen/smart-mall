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
@TableName("pms_sku_images")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("skuImages")
    @Schema(description = "sku图片")
public class SkuImages {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("sku_id")
    @Schema(description = "sku_id")
    private Long skuId;

    @TableField("img_url")
    @Schema(description = "图片地址")
    private String imgUrl;

    @TableField("img_sort")
    @Schema(description = "排序")
    private Integer imgSort;

    @TableField("default_img")
    @Schema(description = "默认图[0 - 不是默认图，1 - 是默认图]")
    private Integer defaultImg;
}

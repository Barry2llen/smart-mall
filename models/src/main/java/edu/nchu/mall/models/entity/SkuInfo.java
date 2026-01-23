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

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("pms_sku_info")
@Schema(description = "sku信息")
public class SkuInfo {

    @TableId(value = "sku_id", type = IdType.ASSIGN_ID)
    @Schema(description = "skuId")
    private Long skuId;

    @TableField("spu_id")
    @Schema(description = "spuId")
    private Long spuId;

    @TableField("sku_name")
    @Schema(description = "sku名称")
    private String skuName;

    @TableField("sku_desc")
    @Schema(description = "sku介绍描述")
    private String skuDesc;

    @TableField("catalog_id")
    @Schema(description = "所属分类id")
    private Long catalogId;

    @TableField("brand_id")
    @Schema(description = "品牌id")
    private Long brandId;

    @TableField("sku_default_img")
    @Schema(description = "默认图片")
    private String skuDefaultImg;

    @TableField("sku_title")
    @Schema(description = "标题")
    private String skuTitle;

    @TableField("sku_subtitle")
    @Schema(description = "副标题")
    private String skuSubtitle;

    @TableField("price")
    @Schema(description = "价格")
    private BigDecimal price;

    @TableField("sale_count")
    @Schema(description = "销量")
    private Long saleCount;
}

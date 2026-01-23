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
@TableName("pms_sku_sale_attr_value")
@Schema(description = "sku销售属性&值")
public class SkuSaleAttrValue {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("sku_id")
    @Schema(description = "sku_id")
    private Long skuId;

    @TableField("attr_id")
    @Schema(description = "attr_id")
    private Long attrId;

    @TableField("attr_name")
    @Schema(description = "销售属性名")
    private String attrName;

    @TableField("attr_value")
    @Schema(description = "销售属性值")
    private String attrValue;

    @TableField("attr_sort")
    @Schema(description = "顺序")
    private Integer attrSort;
}

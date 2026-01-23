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
@TableName("wms_ware_sku")
@Schema(description = "商品库存")
public class WareSku {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("sku_id")
    @Schema(description = "sku_id")
    private Long skuId;

    @TableField("ware_id")
    @Schema(description = "仓库id")
    private Long wareId;

    @TableField("stock")
    @Schema(description = "库存数")
    private Integer stock;

    @TableField("sku_name")
    @Schema(description = "sku_name")
    private String skuName;

    @TableField("stock_locked")
    @Schema(description = "锁定库存")
    private Integer stockLocked;
}

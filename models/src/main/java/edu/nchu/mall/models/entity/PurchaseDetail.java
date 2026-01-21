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
@TableName("wms_purchase_detail")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("purchaseDetail")
@Schema(description = "wms_purchase_detail?")
public class PurchaseDetail {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("purchase_id")
    @Schema(description = "采购单id")
    private Long purchaseId;

    @TableField("sku_id")
    @Schema(description = "采购商品id")
    private Long skuId;

    @TableField("sku_num")
    @Schema(description = "采购数量")
    private Integer skuNum;

    @TableField("sku_price")
    @Schema(description = "采购金额")
    private BigDecimal skuPrice;

    @TableField("ware_id")
    @Schema(description = "仓库id")
    private Long wareId;

    @TableField("status")
    @Schema(description = "状态[0新建，1已分配，2正在采购，3已完成，4采购失败]")
    private Integer status;
}

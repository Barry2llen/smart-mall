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
@TableName("wms_ware_order_task_detail")
@Schema(description = "库存工作单")
public class WareOrderTaskDetail {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("sku_id")
    @Schema(description = "sku_id")
    private Long skuId;

    @TableField("sku_name")
    @Schema(description = "sku_name")
    private String skuName;

    @TableField("sku_num")
    @Schema(description = "购买个数")
    private Integer skuNum;

    @TableField("task_id")
    @Schema(description = "工作单id")
    private Long taskId;
}

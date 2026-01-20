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
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("pms_spu_info")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("spuInfo")
    @Schema(description = "spu信息")
public class SpuInfo {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "商品id")
    private Long id;

    @TableField("spu_name")
    @Schema(description = "商品名称")
    private String spuName;

    @TableField("spu_description")
    @Schema(description = "商品描述")
    private String spuDescription;

    @TableField("catalog_id")
    @Schema(description = "所属分类id")
    private Long catalogId;

    @TableField("brand_id")
    @Schema(description = "品牌id")
    private Long brandId;

    @TableField("weight")
    @Schema(description = "weight")
    private BigDecimal weight;

    @TableField("publish_status")
    @Schema(description = "上架状态[0 - 下架，1 - 上架]")
    private Integer publishStatus;

    @TableField("create_time")
    @Schema(description = "create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    @Schema(description = "update_time")
    private LocalDateTime updateTime;
}

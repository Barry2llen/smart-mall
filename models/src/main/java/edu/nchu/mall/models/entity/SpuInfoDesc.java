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
@TableName("pms_spu_info_desc")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("spuInfoDesc")
    @Schema(description = "spu信息介绍")
public class SpuInfoDesc {

    @TableId(value = "spu_id", type = IdType.ASSIGN_ID)
    @Schema(description = "商品id")
    private Long spuId;

    @TableField("decript")
    @Schema(description = "商品介绍")
    private String decript;
}

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
@TableName("pms_spu_images")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("spuImages")
    @Schema(description = "spu图片")
public class SpuImages {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("spu_id")
    @Schema(description = "spu_id")
    private Long spuId;

    @TableField("img_name")
    @Schema(description = "图片名")
    private String imgName;

    @TableField("img_url")
    @Schema(description = "图片地址")
    private String imgUrl;

    @TableField("img_sort")
    @Schema(description = "顺序")
    private Integer imgSort;

    @TableField("default_img")
    @Schema(description = "是否默认图")
    private Integer defaultImg;
}

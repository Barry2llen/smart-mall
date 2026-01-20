
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

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ums_member_collect_spu")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("memberCollectSpu")
@Schema(description = "会员收藏的商品")
public class MemberCollectSpu {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("member_id")
    @Schema(description = "会员id")
    private Long memberId;

    @TableField("spu_id")
    @Schema(description = "spu_id")
    private Long spuId;

    @TableField("spu_name")
    @Schema(description = "spu_name")
    private String spuName;

    @TableField("spu_img")
    @Schema(description = "spu_img")
    private String spuImg;

    @TableField("create_time")
    @Schema(description = "create_time")
    private LocalDateTime createTime;
}

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
@TableName("pms_spu_comment")
@Schema(description = "商品评价")
public class SpuComment {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("sku_id")
    @Schema(description = "sku_id")
    private Long skuId;

    @TableField("spu_id")
    @Schema(description = "spu_id")
    private Long spuId;

    @TableField("spu_name")
    @Schema(description = "商品名字")
    private String spuName;

    @TableField("member_nick_name")
    @Schema(description = "会员昵称")
    private String memberNickName;

    @TableField("star")
    @Schema(description = "星级")
    private Integer star;

    @TableField("member_ip")
    @Schema(description = "会员ip")
    private String memberIp;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("show_status")
    @Schema(description = "显示状态[0-不显示，1-显示]")
    private Integer showStatus;

    @TableField("spu_attributes")
    @Schema(description = "购买时属性组合")
    private String spuAttributes;

    @TableField("likes_count")
    @Schema(description = "点赞数")
    private Integer likesCount;

    @TableField("reply_count")
    @Schema(description = "回复数")
    private Integer replyCount;

    @TableField("resources")
    @Schema(description = "评论图片/视频[json数据；[{type:文件类型,url:资源路径}]]")
    private String resources;

    @TableField("content")
    @Schema(description = "内容")
    private String content;

    @TableField("member_icon")
    @Schema(description = "用户头像")
    private String memberIcon;

    @TableField("comment_type")
    @Schema(description = "评论类型[0 - 对商品的直接评论，1 - 对评论的回复]")
    private Integer commentType;
}

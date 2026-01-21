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
@TableName("sms_home_adv")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("homeAdv")
@Schema(description = "首页轮播广告")
public class HomeAdv {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("name")
    @Schema(description = "名字")
    private String name;

    @TableField("pic")
    @Schema(description = "图片地址")
    private String pic;

    @TableField("start_time")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @TableField("status")
    @Schema(description = "状态")
    private Integer status;

    @TableField("click_count")
    @Schema(description = "点击数")
    private Integer clickCount;

    @TableField("url")
    @Schema(description = "广告详情连接地址")
    private String url;

    @TableField("note")
    @Schema(description = "备注")
    private String note;

    @TableField("sort")
    @Schema(description = "排序")
    private Integer sort;

    @TableField("publisher_id")
    @Schema(description = "发布者")
    private Long publisherId;

    @TableField("auth_id")
    @Schema(description = "审核者")
    private Long authId;
}

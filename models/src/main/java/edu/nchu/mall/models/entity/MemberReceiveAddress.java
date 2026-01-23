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
@TableName("ums_member_receive_address")
@Schema(description = "会员收货地址")
public class MemberReceiveAddress {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("member_id")
    @Schema(description = "member_id")
    private Long memberId;

    @TableField("name")
    @Schema(description = "收货人姓名")
    private String name;

    @TableField("phone")
    @Schema(description = "电话")
    private String phone;

    @TableField("post_code")
    @Schema(description = "邮政编码")
    private String postCode;

    @TableField("province")
    @Schema(description = "省份/直辖市")
    private String province;

    @TableField("city")
    @Schema(description = "城市")
    private String city;

    @TableField("region")
    @Schema(description = "区")
    private String region;

    @TableField("detail_address")
    @Schema(description = "详细地址(街道)")
    private String detailAddress;

    @TableField("areacode")
    @Schema(description = "省市区代码")
    private String areacode;

    @TableField("default_status")
    @Schema(description = "是否默认")
    private Integer defaultStatus;
}

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
@TableName("ums_member_statistics_info")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@c")
@JsonTypeName("memberStatisticsInfo")
    @Schema(description = "会员统计信息")
public class MemberStatisticsInfo {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(description = "id")
    private Long id;

    @TableField("member_id")
    @Schema(description = "会员id")
    private Long memberId;

    @TableField("consume_amount")
    @Schema(description = "累计消费金额")
    private BigDecimal consumeAmount;

    @TableField("coupon_amount")
    @Schema(description = "累计优惠金额")
    private BigDecimal couponAmount;

    @TableField("order_count")
    @Schema(description = "订单数量")
    private Integer orderCount;

    @TableField("coupon_count")
    @Schema(description = "优惠券数量")
    private Integer couponCount;

    @TableField("comment_count")
    @Schema(description = "评价数")
    private Integer commentCount;

    @TableField("return_order_count")
    @Schema(description = "退货数量")
    private Integer returnOrderCount;

    @TableField("login_count")
    @Schema(description = "登录次数")
    private Integer loginCount;

    @TableField("attend_count")
    @Schema(description = "关注数量")
    private Integer attendCount;

    @TableField("fans_count")
    @Schema(description = "粉丝数量")
    private Integer fansCount;

    @TableField("collect_product_count")
    @Schema(description = "收藏的商品数量")
    private Integer collectProductCount;

    @TableField("collect_subject_count")
    @Schema(description = "收藏的专题活动数量")
    private Integer collectSubjectCount;

    @TableField("collect_comment_count")
    @Schema(description = "收藏的评论数量")
    private Integer collectCommentCount;

    @TableField("invite_friend_count")
    @Schema(description = "邀请的朋友数量")
    private Integer inviteFriendCount;
}

package edu.nchu.mall.models.dto;

import edu.nchu.mall.models.validation.Groups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新/新增会员统计信息")
public class MemberStatisticsInfoDTO {

    @Schema(description = "id")
    @Null(groups = Groups.Create.class)
    @NotNull(groups = Groups.Update.class)
    private Long id;

    @Schema(description = "会员id")
    private Long memberId;

    @Schema(description = "累计消费金额")
    private BigDecimal consumeAmount;

    @Schema(description = "累计优惠金额")
    private BigDecimal couponAmount;

    @Schema(description = "订单数量")
    private Integer orderCount;

    @Schema(description = "优惠券数量")
    private Integer couponCount;

    @Schema(description = "评价数")
    private Integer commentCount;

    @Schema(description = "退货数量")
    private Integer returnOrderCount;

    @Schema(description = "登录次数")
    private Integer loginCount;

    @Schema(description = "关注数量")
    private Integer attendCount;

    @Schema(description = "粉丝数量")
    private Integer fansCount;

    @Schema(description = "收藏的商品数量")
    private Integer collectProductCount;

    @Schema(description = "收藏的专题活动数量")
    private Integer collectSubjectCount;

    @Schema(description = "收藏的评论数量")
    private Integer collectCommentCount;

    @Schema(description = "邀请的朋友数量")
    private Integer inviteFriendCount;
}

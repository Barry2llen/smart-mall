package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLevelVO {

    @Schema(description = "会员等级id")
    private Long id;

    @Schema(description = "等级名称")
    private String name;

    @Schema(description = "等级需要的成长值")
    private Integer growthPoint;

    @Schema(description = "是否为默认等级[0->不是；1->是]")
    private Integer defaultStatus;

    @Schema(description = "免运费标准")
    private BigDecimal freeFreightPoint;

    @Schema(description = "每次评价获取的成长值")
    private Integer commentGrowthPoint;

    @Schema(description = "是否有免邮特权")
    private Integer priviledgeFreeFreight;

    @Schema(description = "是否有会员价格特权")
    private Integer priviledgeMemberPrice;

    @Schema(description = "是否有生日特权")
    private Integer priviledgeBirthday;

    @Schema(description = "备注")
    private String note;
}

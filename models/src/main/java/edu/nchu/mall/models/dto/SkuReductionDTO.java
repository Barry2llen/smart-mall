package edu.nchu.mall.models.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "保存SKU优惠和满减信息的DTO")
public class SkuReductionDTO {

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "满减：满几件")
    private Integer fullCount;

    @Schema(description = "满减：打几折（0-1之间）")
    private BigDecimal discount;

    @Schema(description = "满减：是否叠加其他优惠（0-否，1-是）")
    private Integer countStatus;

    @Schema(description = "满减：满多少金额")
    private BigDecimal fullPrice;

    @Schema(description = "满减：减多少金额")
    private BigDecimal reducePrice;

    @Schema(description = "满减：是否叠加其他优惠（0-否，1-是）")
    private Integer priceStatus;

    @Schema(description = "会员价格列表")
    private List<SpuSaveDTO.Skus.MemberPrice> memberPrice;
}

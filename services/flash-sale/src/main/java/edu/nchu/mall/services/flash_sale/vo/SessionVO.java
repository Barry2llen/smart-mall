package edu.nchu.mall.services.flash_sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "秒杀活动场次信息的VO对象")
@NoArgsConstructor
public class SessionVO {
    @Schema(description = "场次id")
    private Long id;

    @Schema(description = "场次名称")
    private String name;

    @Schema(description = "每日开始时间")
    private LocalDateTime startTime;

    @Schema(description = "每日结束时间")
    private LocalDateTime endTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "秒杀活动场次关联的商品信息列表")
    private List<SessionRelatedSkuInfoVO> skuInfos;
}

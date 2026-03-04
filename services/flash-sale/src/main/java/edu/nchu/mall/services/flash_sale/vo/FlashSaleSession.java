package edu.nchu.mall.services.flash_sale.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "秒杀活动场次")
public class FlashSaleSession {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "场次名称")
    private String name;

    @Schema(description = "每日开始时间")
    private LocalDateTime startTime;

    @Schema(description = "每日结束时间")
    private LocalDateTime endTime;

    @Schema(description = "启用状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

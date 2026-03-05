package edu.nchu.mall.services.flash_sale.rentity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "秒杀缓存清理延迟消息")
public class FlashSaleCleanupMessage {

    @Schema(description = "场次ID")
    private Long sessionId;

    @Schema(description = "场次内容摘要")
    private String digest;

    @Schema(description = "计划清理时间")
    private LocalDateTime cleanTime;
}

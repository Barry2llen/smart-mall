package edu.nchu.mall.services.flash_sale.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "秒杀请求DTO")
public class Kill {
    @NotNull
    @Schema(description = "商品ID", required = true)
    private Long skuId;

    @NotNull
    @Schema(description = "秒杀活动场次ID", required = true)
    private Long sessionId;

    @NotNull
    @Schema(description = "随机码", required = true)
    private String randomCode;

    @NotNull
    @Schema(description = "购买数量", required = true)
    private Integer num;

    @Nullable
    @Schema(description = "地址ID，秒杀成功后用于确认订单时的地址选择，非必填，如果不提供则默认使用用户的默认地址", required = false)
    private Long addressId;

    @Nullable
    @Schema(description = "备注信息", required = false)
    private String note;
}

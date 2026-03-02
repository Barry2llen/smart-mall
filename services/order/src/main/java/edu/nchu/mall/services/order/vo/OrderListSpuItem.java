package edu.nchu.mall.services.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "订单列表项（按品牌聚合）")
public class OrderListSpuItem {

    @Schema(description = "spu_id")
    private Long spuId;

    @Schema(description = "spu名")
    private String spuName;

    @Schema(description = "spu图片")
    private String spuPic;

    @Schema(description = "品牌")
    private String spuBrand;

    @Schema(description = "订单列表项")
    List<OrderListSkuItem> spuItems;
}

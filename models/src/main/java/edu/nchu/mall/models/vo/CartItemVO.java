package edu.nchu.mall.models.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "购物项视图")
public class CartItemVO {
    @Schema(description = "skuId")
    private Long skuId;
    @Schema(description = "spuId")
    private Long spuId;
    @Schema(description = "是否选中")
    private Boolean selected = Boolean.TRUE;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "图片")
    private String image;
    @Schema(description = "属性")
    private List<String> skuAttr;
    @Schema(description = "价格")
    private BigDecimal price;
    @Schema(description = "数量")
    private Integer count;
    @Schema(description = "总价")
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(this.count));
    }
}

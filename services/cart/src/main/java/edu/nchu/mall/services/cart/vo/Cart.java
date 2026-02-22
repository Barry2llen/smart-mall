package edu.nchu.mall.services.cart.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "购物车")
public class Cart {
    @Schema(description = "购物项")
    private List<CartItemVO> items;
    @Schema(description = "商品总数")
    private Integer countNumber;
    @Schema(description = "商品类型总数")
    private Integer countType;
    @Schema(description = "总价")
    private BigDecimal totalAmount;
    @Schema(description = "优惠")
    private BigDecimal reduce = BigDecimal.ZERO;

    public List<CartItemVO> getItems() {
        return items == null ? List.of() : items;
    }

    public int getCountNumber() {
        if (items != null && !items.isEmpty()) {
            int count = 0;
            for (CartItemVO item : items) {
                if (item.getSelected()) {
                    count += item.getCount();
                }
            }
            return count;
        }else return 0;
    }

    public int getCountType() {
        if (items != null && !items.isEmpty()) {
            int count = 0;
            for (CartItemVO item : items) {
                if (item.getSelected()) {
                    count += 1;
                }
            }
            return count;
        }else return 0;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = new BigDecimal("0");
        if (items != null && !items.isEmpty()) {
            for (CartItemVO item : items) {
                if (item.getSelected()) {
                    total = total.add(item.getTotalPrice());
                }
            }
        }
        return total.subtract(getReduce());
    }

    public BigDecimal getReduce() {
        // TODO: 计算优惠
        return reduce;
    }
}

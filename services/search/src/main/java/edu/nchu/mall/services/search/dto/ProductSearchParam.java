package edu.nchu.mall.services.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "搜索参数")
@NoArgsConstructor
public class ProductSearchParam {
    @Schema(description = "搜索关键字")
    private String keyword;

    @Schema(description = "商品分类ID")
    private Long catalogId;

    @Schema(description = "排序规则")
    private List<String> sort;

    @Schema(description = "是否有货")
    private Integer hasStock;

    @Schema(description = "价格区间")
    private String skuPrice;

    @Schema(description = "品牌ID")
    private List<Long> brandIds;

    @Schema(description = "属性")
    private List<String> attrs;

    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "页大小")
    private Integer pageSize;
}

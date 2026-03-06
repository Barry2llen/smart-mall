package edu.nchu.mall.services.product.web;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.vo.CategoryVO;
import edu.nchu.mall.models.vo.SkuItemVO;
import edu.nchu.mall.services.product.service.CategoryService;
import edu.nchu.mall.services.product.service.SkuInfoService;
import edu.nchu.mall.services.product.service.SkuSaleAttrValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "商品接口", description = "提供商品相关的API")
@RequestMapping("/public/product")
@RestController
public class ProductWebController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Operation(summary = "以树形结构查出所有分类以及子分类")
    @GetMapping("/category/list")
    public R<List<CategoryVO>> list(){
        return R.success(categoryService.listWithTree());
    }

    @Parameters(@Parameter(name = "sid", description = "Sku主键"))
    @Operation(summary = "获取商品详情")
    @GetMapping("/item/{skuId}")
    public R<SkuItemVO> getSkuItem(@PathVariable Long skuId) {
        SkuItemVO data = skuInfoService.getSkuItem(skuId);
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters(@Parameter(name = "spuId", description = "Spu主键"))
    @Operation(summary = "获取skuId与销售属性值的映射关系（key=attr1Id:attr1Value_attr2Id:attr2Value_...）\n" +
            "（以此类推，其中attrId先按升序排序再组合）\n" +
            "（该map的value为另一种属性组合的skuId）")
    @GetMapping("/spu/{spuId}/sku-attrs-mapping")
    public R<Map<String, Long>> getSpuSkuAttrsMapping(@PathVariable Long spuId) {
        return R.success(skuSaleAttrValueService.getSpuSkuAttrsMapping(spuId));
    }
}

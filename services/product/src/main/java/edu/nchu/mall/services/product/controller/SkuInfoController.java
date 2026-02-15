package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.dto.SkuInfoDTO;
import edu.nchu.mall.models.entity.SkuInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.vo.SkuInfoVO;
import edu.nchu.mall.models.vo.SkuItemVO;
import edu.nchu.mall.services.product.service.SkuInfoService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Sku信息")
@Slf4j
@RestController
@RequestMapping("/sku-infos")
public class SkuInfoController {

    @Autowired
    SkuInfoService skuInfoService;

    @Parameters(@Parameter(name = "sid", description = "SkuInfo主键"))
    @Operation(summary = "获取SkuInfo详情")
    @GetMapping("/{sid}")
    public R<SkuInfoVO> getSkuInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SkuInfoVO data = skuInfoService.getVOById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "body", description = "要更新的SkuInfo字段")
    })
    @Operation(summary = "更新SkuInfo")
    @PutMapping
    public R<?> updateSkuInfo(@RequestBody @Validated SkuInfoDTO body) {
        boolean res = skuInfoService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量"),
            @Parameter(name = "catalogId", description = "分类id（可选）"),
            @Parameter(name = "brandId", description = "品牌id（可选）"),
            @Parameter(name = "key", description = "模糊查询关键字（可选）（id或名字）")
    })
    @Operation(summary = "获取SkuInfo列表")
    @GetMapping("/list")
    public R<List<SkuInfoVO>> listSkuInfo(@RequestParam Integer pageNum,
                                          @RequestParam Integer pageSize,
                                          @RequestParam(required = false) Long catalogId,
                                          @RequestParam(required = false) Long brandId,
                                          @RequestParam(required = false) String key,
                                          @RequestParam(required = false) BigDecimal minPrice,
                                          @RequestParam(required = false) BigDecimal maxPrice) {
        return R.success(skuInfoService.list(pageNum, pageSize, catalogId, brandId, key, minPrice, maxPrice));
    }

    @Parameters(@Parameter(name = "sid", description = "Sku主键"))
    @Operation(summary = "获取商品详情")
    @GetMapping("/item/{sid}")
    public R<SkuItemVO> getSkuItem(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SkuItemVO data = skuInfoService.getSkuItem(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }
}

package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.dto.SpuInfoDTO;
import edu.nchu.mall.models.dto.SpuSaveDTO;
import edu.nchu.mall.models.entity.SpuInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.vo.SpuInfoVO;
import edu.nchu.mall.services.product.service.SpuInfoService;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Tag(name = "Spu信息")
@Slf4j
@RestController
@RequestMapping("/spu-infos")
public class SpuInfoController {

    @Autowired
    SpuInfoService spuInfoService;

    @Parameters(@Parameter(name = "spuIds", description = "SpuId列表"))
    @Operation(description = "批量获取Spu信息详情")
    @PostMapping("/batch")
    public R<Map<Long, SpuInfoVO>> getSpuInfoBatch(@RequestBody Collection<Long> spuIds) {
        return R.success(spuInfoService.getBatchSpuInfo(spuIds));
    }

    @Parameters(@Parameter(name = "sid", description = "Spu信息主键"))
    @Operation(summary = "获取Spu信息详情")
    @GetMapping("/{sid}")
    public R<SpuInfo> getSpuInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SpuInfo data = spuInfoService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters(@Parameter(name = "body", description = "新增的Spu信息"))
    @Operation(summary = "创建Spu信息")
    @PostMapping
    public R<?> saveSpuInfo(@RequestBody SpuSaveDTO dto) {
        boolean res = spuInfoService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters({
            @Parameter(name = "dto", description = "要更新的Spu信息字段")
    })
    @Operation(summary = "更新spu信息")
    @PutMapping
    public R<?> updateSpuInfo(@RequestBody @Validated SpuInfoDTO dto) {
        boolean res = spuInfoService.updateById(dto);
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
            @Parameter(name = "key", description = "模糊查询关键字（可选）（id或名字）"),
            @Parameter(name = "status", description = "上架状态（可选）")
    })
    @Operation(summary = "获取spu信息列表")
    @GetMapping("/list")
    public R<List<SpuInfo>> listSpuInfo(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize,
                                        @RequestParam(required = false) Long catalogId,
                                        @RequestParam(required = false) Long brandId,
                                        @RequestParam(required = false) String key,
                                        @RequestParam(required = false) Integer status) {
        return R.success(spuInfoService.list(pageNum, pageSize, catalogId, brandId, key, status));
    }
}

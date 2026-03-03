package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.SpuImagesService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@Hidden
@Tag(name = "SpuImages")
@Slf4j
@RestController
@RequestMapping("/spu-images")
public class SpuImagesController {

    @Autowired
    SpuImagesService spuImagesService;

    @Parameters(@Parameter(name = "spuIds", description = "Spu id集合", required = true))
    @Operation(description = "批量获取默认Spu图片")
    @PostMapping("/default/batch")
    public R<Map<Long, String>> getSpuDefaultImagesBatch(@RequestBody Collection<Long> spuIds) {
        Map<Long, String> res = spuImagesService.getSpuDefaultImagesBatch(spuIds);
        return new R<>(RCT.SUCCESS, "success", res);
    }
}

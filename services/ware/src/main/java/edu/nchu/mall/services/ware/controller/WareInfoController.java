package edu.nchu.mall.services.ware.controller;

import edu.nchu.mall.models.entity.WareInfo;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.services.ware.service.WareInfoService;
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

import java.util.List;

@Tag(name = "仓库信息")
@Slf4j
@RestController
@RequestMapping("/ware-infos")
public class WareInfoController {

    @Autowired
    WareInfoService wareInfoService;

    @Parameters(@Parameter(name = "sid", description = "WareInfo主键"))
    @Operation(summary = "获取WareInfo详情")
    @GetMapping("/{sid}")
    public R<WareInfo> getWareInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        WareInfo data = wareInfoService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "WareInfo主键"),
            @Parameter(name = "body", description = "更新后的WareInfo")
    })
    @Operation(summary = "更新WareInfo")
    @PutMapping("/{sid}")
    public R<?> updateWareInfo(@RequestBody @Validated(Groups.Update.class) WareInfo body) {
        boolean res = wareInfoService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的WareInfo"))
    @Operation(summary = "创建WareInfo")
    @PostMapping
    public R<?> createWareInfo(@RequestBody @Validated(Groups.Create.class) WareInfo body) {
        boolean res = wareInfoService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "WareInfo主键"))
    @Operation(summary = "删除WareInfo")
    @DeleteMapping("/{sid}")
    public R<?> deleteWareInfo(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = wareInfoService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量"),
            @Parameter(name = "key", description = "搜索关键字（可选）（id或仓库名）")
    })
    @Operation(summary = "分页获取仓库信息列表")
    @GetMapping("/list")
    public R<List<WareInfo>> list(@RequestParam Integer pageNum, @RequestParam Integer pageSize,
                                  @RequestParam(required = false) String key ){
        return R.success(wareInfoService.list(pageNum, pageSize, key));
    }
}

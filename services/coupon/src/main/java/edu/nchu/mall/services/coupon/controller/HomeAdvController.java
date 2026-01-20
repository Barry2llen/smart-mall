package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.HomeAdv;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.HomeAdvService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "HomeAdv")
@Slf4j
@RestController
@RequestMapping("/home-advs")
public class HomeAdvController {

    @Autowired
    HomeAdvService homeAdvService;

    @Parameters(@Parameter(name = "sid", description = "HomeAdv主键"))
    @Operation(summary = "获取HomeAdv详情")
    @GetMapping("/{sid}")
    public R<?> getHomeAdv(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        HomeAdv data = homeAdvService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "HomeAdv主键"),
            @Parameter(name = "body", description = "更新后的HomeAdv")
    })
    @Operation(summary = "更新HomeAdv")
    @PutMapping("/{sid}")
    public R<?> updateHomeAdv(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody HomeAdv body) {
        body.setId(Long.parseLong(sid));
        boolean res = homeAdvService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的HomeAdv"))
    @Operation(summary = "创建HomeAdv")
    @PostMapping
    public R<?> createHomeAdv(@RequestBody HomeAdv body) {
        body.setId(null);
        boolean res = homeAdvService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "HomeAdv主键"))
    @Operation(summary = "删除HomeAdv")
    @DeleteMapping("/{sid}")
    public R<?> deleteHomeAdv(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = homeAdvService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}

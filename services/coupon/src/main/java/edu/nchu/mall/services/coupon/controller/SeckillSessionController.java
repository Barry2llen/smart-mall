package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.SeckillSession;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.SeckillSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "SeckillSession")
@Slf4j
@RestController
@RequestMapping("/seckill-sessions")
public class SeckillSessionController {

    @Autowired
    SeckillSessionService seckillSessionService;

    @Parameters(@Parameter(name = "sid", description = "SeckillSession主键"))
    @Operation(summary = "获取SeckillSession详情")
    @GetMapping("/{sid}")
    public R<?> getSeckillSession(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        SeckillSession data = seckillSessionService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "SeckillSession主键"),
            @Parameter(name = "body", description = "更新后的SeckillSession")
    })
    @Operation(summary = "更新SeckillSession")
    @PutMapping("/{sid}")
    public R<?> updateSeckillSession(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody SeckillSession body) {
        body.setId(Long.parseLong(sid));
        boolean res = seckillSessionService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的SeckillSession"))
    @Operation(summary = "创建SeckillSession")
    @PostMapping
    public R<?> createSeckillSession(@RequestBody SeckillSession body) {
        body.setId(null);
        boolean res = seckillSessionService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "SeckillSession主键"))
    @Operation(summary = "删除SeckillSession")
    @DeleteMapping("/{sid}")
    public R<?> deleteSeckillSession(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = seckillSessionService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}

package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.HomeSubjectSpu;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.HomeSubjectSpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "HomeSubjectSpu")
@Slf4j
@RestController
@RequestMapping("/home-subject-spus")
public class HomeSubjectSpuController {

    @Autowired
    HomeSubjectSpuService homeSubjectSpuService;

    @Parameters(@Parameter(name = "sid", description = "HomeSubjectSpu主键"))
    @Operation(summary = "获取HomeSubjectSpu详情")
    @GetMapping("/{sid}")
    public R<?> getHomeSubjectSpu(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        HomeSubjectSpu data = homeSubjectSpuService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "HomeSubjectSpu主键"),
            @Parameter(name = "body", description = "更新后的HomeSubjectSpu")
    })
    @Operation(summary = "更新HomeSubjectSpu")
    @PutMapping("/{sid}")
    public R<?> updateHomeSubjectSpu(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody HomeSubjectSpu body) {
        body.setId(Long.parseLong(sid));
        boolean res = homeSubjectSpuService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的HomeSubjectSpu"))
    @Operation(summary = "创建HomeSubjectSpu")
    @PostMapping
    public R<?> createHomeSubjectSpu(@RequestBody HomeSubjectSpu body) {
        body.setId(null);
        boolean res = homeSubjectSpuService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "HomeSubjectSpu主键"))
    @Operation(summary = "删除HomeSubjectSpu")
    @DeleteMapping("/{sid}")
    public R<?> deleteHomeSubjectSpu(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = homeSubjectSpuService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}

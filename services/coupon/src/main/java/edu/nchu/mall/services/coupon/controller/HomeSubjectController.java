package edu.nchu.mall.services.coupon.controller;

import edu.nchu.mall.models.entity.HomeSubject;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.coupon.service.HomeSubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "HomeSubject")
@Slf4j
@RestController
@RequestMapping("/home-subjects")
public class HomeSubjectController {

    @Autowired
    HomeSubjectService homeSubjectService;

    @Parameters(@Parameter(name = "sid", description = "HomeSubject主键"))
    @Operation(summary = "获取HomeSubject详情")
    @GetMapping("/{sid}")
    public R<?> getHomeSubject(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        HomeSubject data = homeSubjectService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "HomeSubject主键"),
            @Parameter(name = "body", description = "更新后的HomeSubject")
    })
    @Operation(summary = "更新HomeSubject")
    @PutMapping("/{sid}")
    public R<?> updateHomeSubject(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody HomeSubject body) {
        body.setId(Long.parseLong(sid));
        boolean res = homeSubjectService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的HomeSubject"))
    @Operation(summary = "创建HomeSubject")
    @PostMapping
    public R<?> createHomeSubject(@RequestBody HomeSubject body) {
        body.setId(null);
        boolean res = homeSubjectService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "HomeSubject主键"))
    @Operation(summary = "删除HomeSubject")
    @DeleteMapping("/{sid}")
    public R<?> deleteHomeSubject(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = homeSubjectService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}

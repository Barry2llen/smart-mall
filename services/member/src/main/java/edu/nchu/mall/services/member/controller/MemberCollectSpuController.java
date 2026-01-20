package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.MemberCollectSpu;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.MemberCollectSpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MemberCollectSpu")
@Slf4j
@RestController
@RequestMapping("/member-collect-spus")
public class MemberCollectSpuController {

    @Autowired
    MemberCollectSpuService memberCollectSpuService;

    @Parameters(@Parameter(name = "sid", description = "MemberCollectSpu主键"))
    @Operation(summary = "获取MemberCollectSpu详情")
    @GetMapping("/{sid}")
    public R<?> getMemberCollectSpu(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberCollectSpu data = memberCollectSpuService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "MemberCollectSpu主键"),
            @Parameter(name = "body", description = "更新后的MemberCollectSpu")
    })
    @Operation(summary = "更新MemberCollectSpu")
    @PutMapping("/{sid}")
    public R<?> updateMemberCollectSpu(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody MemberCollectSpu body) {
        body.setId(Long.parseLong(sid));
        boolean res = memberCollectSpuService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的MemberCollectSpu"))
    @Operation(summary = "创建MemberCollectSpu")
    @PostMapping
    public R<?> createMemberCollectSpu(@RequestBody MemberCollectSpu body) {
        body.setId(null);
        boolean res = memberCollectSpuService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "MemberCollectSpu主键"))
    @Operation(summary = "删除MemberCollectSpu")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberCollectSpu(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberCollectSpuService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}

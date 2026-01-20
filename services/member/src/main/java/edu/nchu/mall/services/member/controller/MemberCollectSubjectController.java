package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.entity.MemberCollectSubject;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.member.service.MemberCollectSubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MemberCollectSubject")
@Slf4j
@RestController
@RequestMapping("/member-collect-subjects")
public class MemberCollectSubjectController {

    @Autowired
    MemberCollectSubjectService memberCollectSubjectService;

    @Parameters(@Parameter(name = "sid", description = "MemberCollectSubject主键"))
    @Operation(summary = "获取MemberCollectSubject详情")
    @GetMapping("/{sid}")
    public R<?> getMemberCollectSubject(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberCollectSubject data = memberCollectSubjectService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "MemberCollectSubject主键"),
            @Parameter(name = "body", description = "更新后的MemberCollectSubject")
    })
    @Operation(summary = "更新MemberCollectSubject")
    @PutMapping("/{sid}")
    public R<?> updateMemberCollectSubject(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody MemberCollectSubject body) {
        body.setId(Long.parseLong(sid));
        boolean res = memberCollectSubjectService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的MemberCollectSubject"))
    @Operation(summary = "创建MemberCollectSubject")
    @PostMapping
    public R<?> createMemberCollectSubject(@RequestBody MemberCollectSubject body) {
        body.setId(null);
        boolean res = memberCollectSubjectService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "MemberCollectSubject主键"))
    @Operation(summary = "删除MemberCollectSubject")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberCollectSubject(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberCollectSubjectService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}

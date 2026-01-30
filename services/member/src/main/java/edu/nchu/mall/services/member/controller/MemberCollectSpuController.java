package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.dto.MemberCollectSpuDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.MemberCollectSpuVO;
import edu.nchu.mall.services.member.service.MemberCollectSpuService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "会员收藏商品管理")
@Slf4j
@RestController
@RequestMapping("/member-collect-spus")
public class MemberCollectSpuController {

    @Autowired
    MemberCollectSpuService memberCollectSpuService;

    @Parameters(@Parameter(name = "sid", description = "会员收藏商品主键"))
    @Operation(summary = "获取会员收藏商品详情")
    @GetMapping("/{sid}")
    public R<MemberCollectSpuVO> getMemberCollectSpu(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberCollectSpuVO data = memberCollectSpuService.getMemberCollectSpuById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "dto", description = "更新的会员收藏商品信息")
    })
    @Operation(summary = "更新会员收藏商品")
    @PutMapping
    public R<?> updateMemberCollectSpu(@RequestBody @Validated(Groups.Update.class) MemberCollectSpuDTO dto) {
        boolean res = memberCollectSpuService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "dto", description = "新增的会员收藏商品"))
    @Operation(summary = "创建会员收藏商品")
    @PostMapping
    public R<?> createMemberCollectSpu(@RequestBody @Validated(Groups.Create.class) MemberCollectSpuDTO dto) {
        boolean res = memberCollectSpuService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "会员收藏商品主键"))
    @Operation(summary = "删除会员收藏商品")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberCollectSpu(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberCollectSpuService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取会员收藏商品列表")
    @GetMapping
    public R<List<MemberCollectSpuVO>> list(@RequestParam @Valid @NotNull Integer pageNum,
                                           @RequestParam @Valid @NotNull Integer pageSize) {
        return R.success(memberCollectSpuService.getMemberCollectSpus(pageNum, pageSize));
    }
}

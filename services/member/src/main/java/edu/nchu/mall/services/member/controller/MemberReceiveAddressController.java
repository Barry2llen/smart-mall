package edu.nchu.mall.services.member.controller;

import edu.nchu.mall.models.dto.MemberReceiveAddressDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.MemberReceiveAddressVO;
import edu.nchu.mall.services.member.service.MemberReceiveAddressService;
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

@Tag(name = "会员收货地址管理")
@Slf4j
@RestController
@RequestMapping("/member-receive-addresss")
public class MemberReceiveAddressController {

    @Autowired
    MemberReceiveAddressService memberReceiveAddressService;

    @Parameters(@Parameter(name = "sid", description = "会员收货地址主键"))
    @Operation(summary = "获取会员收货地址详情")
    @GetMapping("/{sid}")
    public R<MemberReceiveAddressVO> getMemberReceiveAddress(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        MemberReceiveAddressVO data = memberReceiveAddressService.getMemberReceiveAddressById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "dto", description = "更新的会员收货地址信息")
    })
    @Operation(summary = "更新会员收货地址")
    @PutMapping
    public R<?> updateMemberReceiveAddress(@RequestBody @Validated(Groups.Update.class) MemberReceiveAddressDTO dto) {
        boolean res = memberReceiveAddressService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "dto", description = "新增的会员收货地址"))
    @Operation(summary = "创建会员收货地址")
    @PostMapping
    public R<?> createMemberReceiveAddress(@RequestBody @Validated(Groups.Create.class) MemberReceiveAddressDTO dto) {
        boolean res = memberReceiveAddressService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "会员收货地址主键"))
    @Operation(summary = "删除会员收货地址")
    @DeleteMapping("/{sid}")
    public R<?> deleteMemberReceiveAddress(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = memberReceiveAddressService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量")
    })
    @Operation(summary = "获取会员收货地址列表")
    @GetMapping
    public R<List<MemberReceiveAddressVO>> list(@RequestParam @Valid @NotNull Integer pageNum,
                                               @RequestParam @Valid @NotNull Integer pageSize) {
        return R.success(memberReceiveAddressService.getMemberReceiveAddresses(pageNum, pageSize));
    }
}

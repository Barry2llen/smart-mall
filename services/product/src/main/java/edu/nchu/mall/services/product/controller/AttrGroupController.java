package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.components.annotation.NotNullCollection;
import edu.nchu.mall.models.dto.AttrGroupDTO;
import edu.nchu.mall.models.entity.AttrGroup;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.AttrGroupVO;
import edu.nchu.mall.services.product.service.AttrGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "属性分组")
@Slf4j
@RestController
@RequestMapping("/attr-groups")
public class AttrGroupController {

    @Autowired
    AttrGroupService attrGroupService;

    @Parameters(@Parameter(name = "sid", description = "分类id"))
    @Operation(summary = "根据分类id获取属性分组及其关联的属性")
    @GetMapping("/by-catelog/{sid}")
    public R<List<AttrGroupVO>> getAttrGroupByCatelogId(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        List<AttrGroupVO> data = attrGroupService.getAttrGroupByCatelogId(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters(@Parameter(name = "sid", description = "属性分组主键"))
    @Operation(summary = "获取属性分组详情")
    @GetMapping("/{sid}")
    public R<AttrGroup> getAttrGroup(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        AttrGroup data = attrGroupService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "dto", description = "要更新的属性分组信息"),
    })
    @Operation(summary = "更新属性分组")
    @PutMapping
    public R<?> updateAttrGroup(@RequestBody @Validated(Groups.Update.class) AttrGroupDTO dto) {
        AttrGroup entity = new AttrGroup();
        BeanUtils.copyProperties(dto, entity);
        entity.setAttrGroupId(Long.parseLong(dto.getAttrGroupId()));
        boolean res = attrGroupService.updateById(entity);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的属性分组"))
    @Operation(summary = "创建属性分组")
    @PostMapping
    public R<?> createAttrGroup(@RequestBody @Validated(Groups.Create.class) AttrGroupDTO dto) {
        AttrGroup entity = new AttrGroup();
        BeanUtils.copyProperties(dto, entity);
        boolean res = attrGroupService.save(entity);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "属性分组id"))
    @Operation(summary = "删除单个分组")
    @DeleteMapping("/{sid}")
    public R<?> deleteAttrGroup(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = attrGroupService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @DeleteMapping
    public R<?> deleteAttrGroups(@RequestBody @Valid @NotNullCollection List<Long> ids) {
        boolean res = attrGroupService.removeByIds(ids);
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量"),
            @Parameter(name = "attrGroupName", description = "(可选)属性分组名称或分组ID模糊查询"),
            @Parameter(name = "catelogId", description = "(可选)分类id")
    })
    @Operation(summary = "分页获取属性分组列表")
    @GetMapping("/list")
    public R<List<AttrGroup>> listAttrGroups(@RequestParam Integer pageNum,
                                             @RequestParam Integer pageSize,
                                             @RequestParam(required = false) String attrGroupName,
                                             @RequestParam(required = false) Integer catelogId) {
        return R.success(attrGroupService.list(pageNum, pageSize, attrGroupName, catelogId));
    }
}

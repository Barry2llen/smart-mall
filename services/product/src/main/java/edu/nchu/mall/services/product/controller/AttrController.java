package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.dto.AttrDTO;
import edu.nchu.mall.models.entity.Attr;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.validation.Groups;
import edu.nchu.mall.models.vo.AttrVO;
import edu.nchu.mall.services.product.service.AttrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品属性")
@Slf4j
@RestController
@RequestMapping("/attrs")
public class AttrController {

    @Autowired
    AttrService attrService;

    @Parameters(@Parameter(name = "sid", description = "商品属性主键"))
    @Operation(summary = "获取商品属性详情")
    @GetMapping("/{sid}")
    public R<AttrVO> getAttr(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        AttrVO data = attrService.getVoById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "body", description = "更新后的商品属性")
    })
    @Operation(summary = "更新商品属性信息")
    @PutMapping
    public R<?> updateAttr(@RequestBody @Validated(Groups.Update.class) AttrDTO dto) {
        boolean res = attrService.updateById(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的商品属性"))
    @Operation(summary = "创建商品属性")
    @PostMapping
    public R<?> createAttr(@RequestBody @Validated(Groups.Create.class) AttrDTO dto) {
        dto.setAttrId(null);
        boolean res = attrService.save(dto);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "商品属性主键"))
    @Operation(summary = "删除商品属性")
    @DeleteMapping("/{sid}")
    public R<?> deleteAttr(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = attrService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }

    @Parameters(@Parameter(name = "sid", description = "商品属性主键"))
    @Operation(summary = "删除属性与属性分组的关联关系")
    @DeleteMapping("/relation/{sid}")
    public R<?> deleteAttrRelation(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid){
        boolean res = attrService.deleteAttrRelation(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete relation failed");
    }

    @Parameters({
            @Parameter(name = "attrId", description = "属性主键"),
            @Parameter(name = "attrGroupId", description = "属性分组主键")
    })
    @Operation(summary = "新增属性与属性分组的关联关系")
    @PostMapping("/relation")
    public R<?> newAttrRelation(@RequestParam Long attrId, @RequestParam Long attrGroupId){
        boolean res = attrService.newAttrRelation(attrId, attrGroupId);
        if (res) {
            return R.success(null);
        }
        return R.fail("create relation failed");
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量"),
            @Parameter(name = "sid", description = "商品分类主键")
    })
    @Operation(summary = "获取未关联属性列表")
    @GetMapping("/no-relation")
    public R<List<AttrVO>> listNonRelationAttrs(@RequestParam Integer pageNum,
                                                @RequestParam Integer pageSize,
                                                @RequestParam @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        return R.success(attrService.listNonRelationAttrs(pageNum, pageSize, Long.parseLong(sid)));
    }

    @Parameters({
            @Parameter(name = "pageNum", description = "页码"),
            @Parameter(name = "pageSize", description = "每页数量"),
            @Parameter(name = "attrName", description = "(可选)属性分组名称或分组ID模糊查询"),
            @Parameter(name = "catelogId", description = "(可选)分类id")
    })
    @Operation(summary = "分页获取属性分组列表")
    @GetMapping("/list")
    public R<List<AttrVO>> listAttrGroups(@RequestParam Integer pageNum,
                                          @RequestParam Integer pageSize,
                                          @RequestParam(required = false) String attrName,
                                          @RequestParam(required = false) Integer catelogId) {
        return R.success(attrService.list(pageNum, pageSize, attrName, catelogId));
    }

    @Parameters(@Parameter(name = "groupId", description = "属性分组主键"))
    @Operation(summary = "根据属性分组id获取属性列表")
    @GetMapping("/group/{groupId}")
    public R<List<AttrVO>> getByGroupId(@PathVariable Long groupId) {
        List<AttrVO> data = attrService.getVosByGroupId(groupId);
        return new R<>(RCT.SUCCESS, "success", data);
    }
}

package edu.nchu.mall.services.product.controller;

import edu.nchu.mall.models.entity.CommentReplay;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.CommentReplayService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Hidden
@Tag(name = "CommentReplay")
@Slf4j
@RestController
@RequestMapping("/comment-replaies")
public class CommentReplayController {

    @Autowired
    CommentReplayService commentReplayService;

    @Parameters(@Parameter(name = "sid", description = "CommentReplay主键"))
    @Operation(summary = "获取CommentReplay详情")
    @GetMapping("/{sid}")
    public R<?> getCommentReplay(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        CommentReplay data = commentReplayService.getById(Long.parseLong(sid));
        return new R<>(RCT.SUCCESS, "success", data);
    }

    @Parameters({
            @Parameter(name = "sid", description = "CommentReplay主键"),
            @Parameter(name = "body", description = "更新后的CommentReplay")
    })
    @Operation(summary = "更新CommentReplay")
    @PutMapping("/{sid}")
    public R<?> updateCommentReplay(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid,
                               @RequestBody CommentReplay body) {
        body.setId(Long.parseLong(sid));
        boolean res = commentReplayService.updateById(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters(@Parameter(name = "body", description = "新增的CommentReplay"))
    @Operation(summary = "创建CommentReplay")
    @PostMapping
    public R<?> createCommentReplay(@RequestBody CommentReplay body) {
        body.setId(null);
        boolean res = commentReplayService.save(body);
        if (res) {
            return R.success(null);
        }
        return R.fail("create failed");
    }

    @Parameters(@Parameter(name = "sid", description = "CommentReplay主键"))
    @Operation(summary = "删除CommentReplay")
    @DeleteMapping("/{sid}")
    public R<?> deleteCommentReplay(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) {
        boolean res = commentReplayService.removeById(Long.parseLong(sid));
        if (res) {
            return R.success(null);
        }
        return R.fail("delete failed");
    }
}

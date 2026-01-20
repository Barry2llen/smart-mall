package edu.nchu.mall.services.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.nchu.mall.models.entity.User;
import edu.nchu.mall.services.product.dto.UserUpdate;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.product.service.UserService;
import edu.nchu.mall.services.product.vo.UserVO;
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
import org.springframework.web.bind.annotation.*;


@Tag(name = "User")
@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Parameters(
            @Parameter(name = "id", description = "用户ID")
    )
    @Operation(summary = "获取用户信息")
    @GetMapping("/{sid}")
    public R<?> getUser(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid) throws NumberFormatException{
        Long id = Long.parseLong(sid);
        UserVO vo = new UserVO();
        User user = userService.getById(id);
        if(user != null){
            BeanUtils.copyProperties(user, vo);
        }

        return new R<>(RCT.SUCCESS, "success", vo);
    }

    @Parameters({
            @Parameter(name = "sid", description = "用户id"),
            @Parameter(name = "update", description = "需要更新的信息")
    })
    @Operation(summary = "更新用户信息")
    @PutMapping("/{sid}")
    public R<?> updateUser(@PathVariable @Length(max = 20, min = 1) @Pattern(regexp = "^[0-9]*$") String sid, @RequestBody @Valid UserUpdate update) throws NumberFormatException{
        boolean res = userService.updateById(new User(Long.parseLong(sid), update.getUsername(), update.getPassword()));
        if(res){
            return R.success(null);
        }
        return R.fail("update failed");
    }

    @Parameters({
            @Parameter(name = "new_user", description = "新增的用户")
    })
    @Operation(summary = "新增用户")
    @PostMapping
    public R<?> createUser(@RequestBody @Valid UserUpdate new_user) {
        boolean res = userService.save(new User(null, new_user.getUsername(), new_user.getPassword()));
        if(res){
            return R.success(null);
        }
        return R.fail(null);
    }

}

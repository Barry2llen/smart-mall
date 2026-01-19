package edu.nchu.mall.services.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.services.product.dto.UserUpdate;
import edu.nchu.mall.services.product.entity.User;
import edu.nchu.mall.services.product.model.R;
import edu.nchu.mall.services.product.model.RCT;
import edu.nchu.mall.services.product.service.UserService;
import edu.nchu.mall.services.product.utils.RedisConstants;
import edu.nchu.mall.services.product.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    @Value("${server.port}")
    private String port;

    @GetMapping("/hello")
    public String test(){
        return String.format("<h1>%s</h1>", port);
    }

    @Parameters(
            @Parameter(name = "id", description = "用户ID")
    )
    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public R<?> getUser(@PathVariable Long id) throws JsonProcessingException {
        String USER_KEY = RedisConstants.USER_PREFIX + id;

        UserVO vo = null;
        String user_json = stringRedisTemplate.opsForValue().get(USER_KEY);
        if (user_json != null) {
            log.info("从Redis读取User:{}", id);
            if(!user_json.isEmpty()){
                vo = new UserVO();
                User user = mapper.readValue(user_json, User.class);
                BeanUtils.copyProperties(user, vo);
            }
        }else{
            log.info("从MySQL读取User:{}", id);
            User user = userService.user(id);
            if(user != null){
                vo = new UserVO();
                BeanUtils.copyProperties(user, vo);
            }
            stringRedisTemplate.opsForValue().set(USER_KEY, vo != null ? mapper.writeValueAsString(vo) : "", RedisConstants.USER_TTL, TimeUnit.SECONDS);
        }

        return new R<>(RCT.SUCCESS, "success", vo);
    }

    @Transactional
    @PutMapping("/{id}")
    public R<?> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdate update) throws JsonProcessingException {
        String USER_KEY = RedisConstants.USER_PREFIX + id;
        boolean res = userService.updateUser(new User(id, update.getUsername(), update.getPassword()));

        if(res){
            stringRedisTemplate.delete(USER_KEY);
            return R.success(null);
        }
        return R.fail(null);
    }

    @Transactional
    @PostMapping
    public R<?> createUser(@RequestBody @Valid UserUpdate new_user) {
        boolean res = userService.saveUser(new User(null, new_user.getUsername(), new_user.getPassword()));
        if(res){
            return R.success(null);
        }
        return R.fail(null);
    }

}

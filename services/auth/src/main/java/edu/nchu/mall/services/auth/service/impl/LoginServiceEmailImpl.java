package edu.nchu.mall.services.auth.service.impl;

import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.feign.member.MemberFeignClient;
import edu.nchu.mall.components.feign.third_party.ThirdPartyFeignClient;
import edu.nchu.mall.models.dto.MemberDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.auth.constants.RedisConstant;
import edu.nchu.mall.services.auth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginServiceEmailImpl implements LoginService {

    public static final String USER_REGISTER_KEY = "user:register:";

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    ThirdPartyFeignClient thirdPartyFeignClient;

    @Autowired
    MemberFeignClient memberFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public boolean sendCode(String email) {
        R<?> res = thirdPartyFeignClient.sendCode("用户", email, "验证您的邮箱");
        return res.getCode() == RCT.SUCCESS;
    }

    @Override
    public boolean register(String username, String password, String email, String code) {
        R<Boolean> verifyResult = thirdPartyFeignClient.verifyCode(code, email);
        if (!verifyResult.getData()) {
            throw new CustomException("验证码错误", null, HttpStatus.BAD_REQUEST);
        }

        MemberDTO member = new MemberDTO();
        member.setUsername(username);
        member.setEmail(email);
        member.setStatus(1);
        member.setCreateTime(LocalDateTime.now());

        String encodedPassword = passwordEncoder.encode(password);
        member.setPassword(encodedPassword);

        R<?> res = memberFeignClient.createMember(member);

        if (res.getCode() != RCT.SUCCESS) {
            throw new CustomException(res.getMsg(), null, HttpStatus.CONFLICT);
        }

        return true;
    }

    @Override
    public Long login(String username, String password) {
        String saltedPassword = memberFeignClient.getSaltedPassword(username);
        if (saltedPassword == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, saltedPassword)) {
            return null;
        }

        return memberFeignClient.getId(username);
    }

    @Override
    public boolean logout(long userId) {
        String refresh_token = redisTemplate.opsForValue().get(RedisConstant.REFRESH_TOKEN_KEY + userId);
        if (refresh_token != null) {
            redisTemplate.delete(RedisConstant.REFRESH_TOKEN_KEY + refresh_token);
        }
        redisTemplate.delete(RedisConstant.REFRESH_TOKEN_KEY + userId);
        return true;
    }
}

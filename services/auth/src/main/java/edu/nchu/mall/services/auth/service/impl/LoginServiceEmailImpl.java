package edu.nchu.mall.services.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.feign.member.MemberFeignClient;
import edu.nchu.mall.components.feign.third_party.ThirdPartyFeignClient;
import edu.nchu.mall.models.dto.MemberDTO;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.services.auth.service.LoginService;
import feign.FeignException;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginServiceEmailImpl implements LoginService {

    public static final String USER_REGISTER_KEY = "user:register:";

    @Autowired
    ThirdPartyFeignClient thirdPartyFeignClient;

    @Autowired
    MemberFeignClient memberFeignClient;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public boolean sendCode(String email) {
        R<?> res = thirdPartyFeignClient.sendCode("用户", email, "验证您的邮箱");
        return res.getCode() == RCT.SUCCESS;
    }

    @Override
    public boolean register(String username, String password, String email, String code) {
        R<Boolean> verifyResult = thirdPartyFeignClient.verifyCode(code, email);
        if (!verifyResult.getData()) {
            return false;
        }

        MemberDTO member = new MemberDTO();
        member.setUsername(username);
        member.setPassword(password);
        member.setEmail(email);
        member.setStatus(1);
        member.setCreateTime(LocalDateTime.now());

        try{
            R<?> registerResult = memberFeignClient.createMember(member);
            return registerResult.getCode() == RCT.SUCCESS;
        }catch (FeignException e) {
            if (e.status() == HttpStatus.CONFLICT.value()) {
                String responseBody = e.contentUTF8();
                ObjectMapper mapper = new ObjectMapper();
                R<?> r = null;
                try {
                    r = mapper.readValue(responseBody, R.class);
                } catch (Exception ex) {
                    // ...
                }
                if(r != null) throw new CustomException(e.getMessage(), e, HttpStatus.CONFLICT, r);
            }
            throw new CustomException(e.getMessage(), e);
        }
    }
}

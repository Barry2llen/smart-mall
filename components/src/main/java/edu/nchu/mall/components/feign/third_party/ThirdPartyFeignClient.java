package edu.nchu.mall.components.feign.third_party;

import edu.nchu.mall.models.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("third-party")
public interface ThirdPartyFeignClient {
    @GetMapping("/email/send")
    R<?> sendCode(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String subject
    );

    @GetMapping("/email/verify")
    R<Boolean> verifyCode(@RequestParam String code, @RequestParam String email);
}

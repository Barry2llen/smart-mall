package edu.nchu.mall.components.feign.member;

import edu.nchu.mall.models.dto.MemberDTO;
import edu.nchu.mall.models.model.R;
import jakarta.annotation.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("member")
public interface MemberFeignClient {
    @PostMapping("/members")
    R<?> createMember(@RequestBody MemberDTO dto);

    @GetMapping("/members/salt")
    @Nullable String getSaltedPassword(@RequestParam String key);
}

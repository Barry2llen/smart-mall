package edu.nchu.mall.components.feign.member;

import edu.nchu.mall.models.dto.MemberDTO;
import edu.nchu.mall.models.model.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("member")
public interface MemberFeignClient {
    @PostMapping("/members")
    R<?> createMember(@RequestBody MemberDTO dto);
}

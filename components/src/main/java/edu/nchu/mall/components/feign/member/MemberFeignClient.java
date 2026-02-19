package edu.nchu.mall.components.feign.member;

import edu.nchu.mall.models.dto.MemberDTO;
import edu.nchu.mall.models.entity.Member;
import edu.nchu.mall.models.model.R;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@FeignClient("member")
public interface MemberFeignClient {
    @PostMapping("/members")
    R<?> createMember(@RequestBody MemberDTO dto);

    @GetMapping("/members/salt")
    @Nullable
    String getSaltedPassword(@RequestParam String key);

    @GetMapping("/members/putByEmail")
    @Nullable Member putByEmail(@RequestParam @NotNull String email, @RequestParam @Nullable String username);

    @GetMapping("/members/id/{key}")
    @Nullable Long getId(@PathVariable String key);
}

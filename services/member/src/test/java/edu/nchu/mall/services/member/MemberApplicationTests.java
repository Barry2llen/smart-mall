package edu.nchu.mall.services.member;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import edu.nchu.mall.models.entity.Member;
import edu.nchu.mall.models.entity.MemberReceiveAddress;
import edu.nchu.mall.services.member.service.MemberReceiveAddressService;
import edu.nchu.mall.services.member.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootTest
@Disabled("手动灌数测试，避免常规测试误写库")
public class MemberApplicationTests {

    private static final int TARGET_COUNT = 10_000;
    private static final int BATCH_SIZE = 500;
    private static final String USERNAME_PREFIX = "seed_member_";
    private static final String EMAIL_SUFFIX = "@test.local";
    private static final Long MEMBER_LEVEL_ID = 2016849373543706625L;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberReceiveAddressService memberReceiveAddressService;

    @Test
    void seedMembersAndAddresses() {
        Map<String, Member> existingMembers = listSeedMembers().stream()
                .collect(Collectors.toMap(Member::getUsername, Function.identity(), (left, right) -> left, LinkedHashMap::new));

        List<Member> membersToInsert = new ArrayList<>();
        for (int i = 1; i <= TARGET_COUNT; i++) {
            String username = buildUsername(i);
            if (!existingMembers.containsKey(username)) {
                membersToInsert.add(buildMember(i));
            }
        }

        int insertedMembers = batchSaveMembers(membersToInsert);
        Map<String, Member> allSeedMembers = listSeedMembers().stream()
                .collect(Collectors.toMap(Member::getUsername, Function.identity(), (left, right) -> left, LinkedHashMap::new));

        Assertions.assertEquals(TARGET_COUNT, allSeedMembers.size(), "测试会员数量未补齐到 10000");

        Set<Long> memberIdsWithAddress = new LinkedHashSet<>();
        for (List<Long> memberIdBatch : partition(new ArrayList<>(allSeedMembers.values()).stream().map(Member::getId).toList(), BATCH_SIZE)) {
            memberIdsWithAddress.addAll(memberReceiveAddressService.list(
                            Wrappers.<MemberReceiveAddress>lambdaQuery()
                                    .in(MemberReceiveAddress::getMemberId, memberIdBatch)
                                    .select(MemberReceiveAddress::getMemberId))
                    .stream()
                    .map(MemberReceiveAddress::getMemberId)
                    .collect(Collectors.toSet()));
        }

        List<MemberReceiveAddress> addressesToInsert = new ArrayList<>();
        for (int i = 1; i <= TARGET_COUNT; i++) {
            Member member = allSeedMembers.get(buildUsername(i));
            if (member == null) {
                continue;
            }
            if (!memberIdsWithAddress.contains(member.getId())) {
                addressesToInsert.add(buildAddress(member, i));
            }
        }

        int insertedAddresses = batchSaveAddresses(addressesToInsert);
        long finalAddressCount = countAddressesByMemberIds(allSeedMembers.values().stream().map(Member::getId).toList());

        Assertions.assertTrue(finalAddressCount >= TARGET_COUNT, "测试会员地址数量不足 10000");

        System.out.printf(
                "seed members done, existing=%d, insertedMembers=%d, insertedAddresses=%d, finalMembers=%d, finalAddresses=%d%n",
                existingMembers.size(),
                insertedMembers,
                insertedAddresses,
                allSeedMembers.size(),
                finalAddressCount
        );
    }

    private List<Member> listSeedMembers() {
        return memberService.list(Wrappers.<Member>lambdaQuery()
                .likeRight(Member::getUsername, USERNAME_PREFIX)
                .select(
                        Member::getId,
                        Member::getUsername,
                        Member::getMobile,
                        Member::getEmail
                ));
    }

    private int batchSaveMembers(List<Member> members) {
        if (members.isEmpty()) {
            return 0;
        }
        for (List<Member> batch : partition(members, BATCH_SIZE)) {
            memberService.saveBatch(batch, BATCH_SIZE);
        }
        return members.size();
    }

    private int batchSaveAddresses(List<MemberReceiveAddress> addresses) {
        if (addresses.isEmpty()) {
            return 0;
        }
        for (List<MemberReceiveAddress> batch : partition(addresses, BATCH_SIZE)) {
            memberReceiveAddressService.saveBatch(batch, BATCH_SIZE);
        }
        return addresses.size();
    }

    private long countAddressesByMemberIds(List<Long> memberIds) {
        long count = 0L;
        for (List<Long> batch : partition(memberIds, BATCH_SIZE)) {
            count += memberReceiveAddressService.count(Wrappers.<MemberReceiveAddress>lambdaQuery()
                    .in(MemberReceiveAddress::getMemberId, batch));
        }
        return count;
    }

    private Member buildMember(int index) {
        Member member = new Member();
        member.setUsername(buildUsername(index));
        member.setPassword("123456");
        member.setNickname("测试用户" + formatIndex(index));
        member.setLevelId(MEMBER_LEVEL_ID);
        member.setMobile(String.format("139%08d", index));
        member.setEmail(buildUsername(index) + EMAIL_SUFFIX);
        member.setHeader("https://example.com/avatar/" + formatIndex(index) + ".png");
        member.setGender(index % 2);
        member.setBirth(LocalDate.of(1990, 1, 1).plusDays(index % 365L));
        member.setCity("南昌市");
        member.setJob("测试会员");
        member.setSign("seed member " + formatIndex(index));
        member.setSourceType(1);
        member.setIntegration(0);
        member.setGrowth(0);
        member.setStatus(1);
        member.setCreateTime(LocalDateTime.of(2024, 1, 1, 0, 0).plusSeconds(index));
        return member;
    }

    private MemberReceiveAddress buildAddress(Member member, int index) {
        MemberReceiveAddress address = new MemberReceiveAddress();
        address.setMemberId(member.getId());
        address.setName("测试收货人" + formatIndex(index));
        address.setPhone(member.getMobile());
        address.setPostCode(String.format("%06d", (100000 + index) % 1000000));
        address.setProvince("江西省");
        address.setCity("南昌市");
        address.setRegion("红谷滩区");
        address.setDetailAddress("测试街道" + formatIndex(index) + "号");
        address.setAreacode("360100");
        address.setDefaultStatus(1);
        return address;
    }

    private String buildUsername(int index) {
        return USERNAME_PREFIX + formatIndex(index);
    }

    private String formatIndex(int index) {
        return String.format("%05d", index);
    }

    private static <T> List<List<T>> partition(List<T> source, int size) {
        List<List<T>> partitions = new ArrayList<>();
        if (source.isEmpty()) {
            return partitions;
        }
        for (int i = 0; i < source.size(); i += size) {
            partitions.add(source.subList(i, Math.min(i + size, source.size())));
        }
        return partitions;
    }
}

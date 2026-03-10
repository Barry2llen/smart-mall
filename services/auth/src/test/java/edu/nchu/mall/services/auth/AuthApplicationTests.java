package edu.nchu.mall.services.auth;

import edu.nchu.mall.components.feign.member.MemberFeignClient;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.vo.MemberVO;
import edu.nchu.mall.services.auth.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class AuthApplicationTests {

    private static final int PAGE_SIZE = 500;
    private static final String CSV_FILE_NAME = "member-access-tokens.csv";

    @Autowired
    private MemberFeignClient memberFeignClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void contextLoads() {

    }

    @Test
    @Disabled("手动导出会员永久 access token，避免常规测试误拉远程服务并落盘")
    void exportPermanentAccessTokensToCsv() throws IOException {
        List<MemberVO> members = listAllMembers();
        Assertions.assertFalse(members.isEmpty(), "未获取到任何会员数据");

        Path outputPath = resolveOutputPath();
        Files.createDirectories(outputPath.getParent());

        int writtenRows = 0;
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("memberId,username,email,mobile,token");
            writer.newLine();

            for (MemberVO member : members) {
                String token = jwtUtils.generatePermanentAccessToken(String.valueOf(member.getId()), Map.of());
                Claims claims = jwtUtils.getClaimsFromToken(token);
                Assertions.assertEquals(String.valueOf(member.getId()), claims.getSubject(), "token subject 与 memberId 不一致");
                Assertions.assertEquals("access", claims.get("type", String.class), "token type 不是 access");
                Assertions.assertNotNull(claims.getIssuedAt(), "永久 token 缺少 iat");
                Assertions.assertNull(claims.getExpiration(), "永久 token 不应包含 exp");

                writer.write(csvRow(
                        String.valueOf(member.getId()),
                        member.getUsername(),
                        member.getEmail(),
                        member.getMobile(),
                        token
                ));
                writer.newLine();
                writtenRows++;
            }
        }

        System.out.printf("exported %d member tokens to %s%n", writtenRows, outputPath.toAbsolutePath());
        Assertions.assertEquals(members.size(), writtenRows, "CSV 写入行数与会员数量不一致");
    }

    private List<MemberVO> listAllMembers() {
        Map<Long, MemberVO> allMembers = new LinkedHashMap<>();
        int pageNum = 1;

        while (true) {
            R<List<MemberVO>> response = memberFeignClient.getMembers(pageNum, PAGE_SIZE);
            Assertions.assertNotNull(response, "member 服务返回为空");
            Assertions.assertTrue(R.succeeded(response), "member 服务查询失败: " + response.getMsg());

            List<MemberVO> data = response.getData();
            if (data == null || data.isEmpty()) {
                break;
            }

            int sizeBefore = allMembers.size();
            for (MemberVO member : data) {
                if (member != null && member.getId() != null) {
                    allMembers.putIfAbsent(member.getId(), member);
                }
            }

            if (allMembers.size() == sizeBefore) {
                break;
            }

            if (data.size() < PAGE_SIZE) {
                break;
            }
            pageNum++;
        }

        return new ArrayList<>(allMembers.values());
    }

    private Path resolveOutputPath() {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        if (cwd.endsWith(Path.of("services", "auth"))) {
            return cwd.resolve("target").resolve(CSV_FILE_NAME);
        }
        return cwd.resolve("services").resolve("auth").resolve("target").resolve(CSV_FILE_NAME);
    }

    private String csvRow(String... values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(csvCell(values[i]));
        }
        return builder.toString();
    }

    private String csvCell(String value) {
        if (value == null) {
            return "";
        }
        boolean needQuote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needQuote ? "\"" + escaped + "\"" : escaped;
    }
}

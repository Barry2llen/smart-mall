package edu.nchu.mall.services.search;

import edu.nchu.mall.services.search.document.User;
import edu.nchu.mall.services.search.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

@Slf4j
@SpringBootTest
public class SearchApplicationTests {

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Autowired
    UserService userService;

    @Test
    void testSaveUser() {
        User user = new User();
        user.setName("张三");
        user.setUsername("zhangsan");
        user.setPassword("<PASSWORD>");
        user.setAddress("上海");
        user.setRegistryDate(new java.util.Date());
        user.setAge(18);
        user.setEmail("<EMAIL>@qq.com");
        boolean res = userService.save(user);
        log.info("保存用户结果：{}", res);
    }

    @Test
    void testQuery() {
        Criteria criteria = Criteria.where("name").contains("张三").or().subCriteria(
                Criteria.where("age").is(18)
        );
        Query query = new CriteriaQuery(criteria);
        SearchHits<User> reslut = elasticsearchOperations.search(query, User.class);
        log.warn("Searched {} elements:", reslut.getTotalHits());
        for (SearchHit<User> hit : reslut) {
            log.warn("Element id {} : {}", hit.getId(), hit.getContent());
        }
    }
}

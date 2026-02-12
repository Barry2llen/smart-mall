package edu.nchu.mall.services.search;

import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.document.User;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import edu.nchu.mall.services.search.service.ProductService;
import edu.nchu.mall.services.search.service.UserService;
import edu.nchu.mall.services.search.utils.QueryUtils;
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
import org.springframework.http.HttpStatus;

import java.util.List;

@Slf4j
@SpringBootTest
public class SearchApplicationTests {

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    //@Test
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

    //@Test
    void testQuery() {
        Criteria.where("name").contains("张三");
        Criteria criteria = Criteria.or().subCriteria(
                Criteria.where("age").is(18)
        );
        Query query = new CriteriaQuery(criteria);
        SearchHits<User> reslut = elasticsearchOperations.search(query, User.class);
        log.warn("Searched {} elements:", reslut.getTotalHits());
        for (SearchHit<User> hit : reslut) {
            log.warn("Element id {} : {}", hit.getId(), hit.getContent());
        }
    }


    @Test
    void testProductQuery() {
        ProductSearchParam param = new ProductSearchParam();
        param.setKeyword("iPhone");
//        param.setHasStock(1);
        param.setCatalogId(225L);
        //param.setBrandIds(List.of(1L));
//        param.setAttrs(List.of("2017506484590100481_iPhone 17"));
        param.setSkuPrice("6000_10000");

        Query query = null;
        QueryUtils.ProductQuery productQuery = new QueryUtils.ProductQuery();
        try{
            query = productQuery.buildQuery(param);
        }catch (Exception e){
            throw new CustomException("封装商品查询请求失败", e, HttpStatus.BAD_REQUEST);
        }

        SearchHits<Product> result = elasticsearchOperations.search(query, Product.class);
        for (SearchHit<Product> hit : result) {
            log.warn("Element id {} : {}", hit.getId(), hit.getContent());
        }
    }

    @Test
    void testProductService() {
        ProductSearchParam param = new ProductSearchParam();
        param.setKeyword("iPhone");
        param.setHasStock(0);
        param.setCatalogId(225L);
        param.setAttrs(List.of("2017506484590100481_iPhone Air:iPhone 17"));
        param.setSkuPrice("_6000");

        Query query = null;
        QueryUtils.ProductQuery productQuery = new QueryUtils.ProductQuery();
        try{
            query = productQuery.buildQuery(param);
        }catch (Exception e){
            throw new CustomException("封装商品查询请求失败", e, HttpStatus.BAD_REQUEST);
        }

        ProductSearchResult result = productService.search(param);
        log.warn("Search hits: {}", result.getTotal());
        log.warn("Search response: {}", result);
    }

}

package edu.nchu.mall.services.search.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import edu.nchu.mall.services.search.repository.ProductRepository;
import edu.nchu.mall.services.search.service.ProductService;
import edu.nchu.mall.services.search.utils.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository repository;

    @Override
    public void save(Product product) {
        repository.save(product);
    }

    @Override
    public void saveAll(Iterable<Product> products) {
        repository.saveAll(products);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll(Iterable<String> ids) {
        repository.deleteAllById(ids);
    }

    @Override
    public ProductSearchResult search(ProductSearchParam param) {
        // TODO 实现检索商品


        Query query = null;
        QueryUtils.ProductQuery productQuery = new QueryUtils.ProductQuery();
        try{
            query = productQuery.buildQuery(param);
        }catch (Exception e){
            throw new CustomException("封装商品查询请求失败", e, HttpStatus.BAD_REQUEST);
        }


        return null;
    }
}

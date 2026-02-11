package edu.nchu.mall.services.search.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import edu.nchu.mall.services.search.repository.ProductRepository;
import edu.nchu.mall.services.search.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository repository;

    private Query buildQuery(ProductSearchParam param) {
        NativeQueryBuilder builder = new NativeQueryBuilder();
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (param.getKeyword() != null && !param.getKeyword().trim().isEmpty()) {
            builder.withHighlightQuery(
                    new HighlightQuery(
                            new Highlight(
                                    HighlightParameters.builder()
                                            .withPreTags("<b style='color:red;'>")
                                            .withPostTags("</b>")
                                            .build()
                                    ,
                                    List.of(new HighlightField("skuPrice"))
                            ),
                            Product.class
                    )
            );
            boolQueryBuilder.must(q -> q.match(m -> m.field("skuTitle").query(param.getKeyword()) ));
        }

        if (param.getCatalogId() != null) {
            boolQueryBuilder.filter(q -> q.term(t -> t.field("catalogId").value(param.getCatalogId())));
        }

        if (param.getBrandIds() != null && !param.getBrandIds().isEmpty()) {
            String json = String.format("{\"brandId\":%s}", param.getBrandIds());
            boolQueryBuilder.filter(q -> q.terms(t -> t.withJson(new StringReader(json))));
        }

        if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {
            Map<Long, List<String>> attrs = new HashMap<>();
            for (String attr : param.getAttrs()) {
                String[] split = attr.split("_");
                if (split.length == 2) {
                    var op = KeyUtils.parseKey2Long(split[0]);
                    if (op.isPresent()) {
                        Long attrId = op.get();
                        String attrValue = split[1];
                        if (attrs.containsKey(attrId)) {
                            attrs.get(attrId).add(attrValue);
                        } else {
                            attrs.put(attrId, new ArrayList<>(List.of(attrValue)));
                        }
                    }
                }
            }

            List<co.elastic.clients.elasticsearch._types.query_dsl.Query> queries = attrs.entrySet().stream().map(entry -> {
                var term = QueryBuilders
                        .term(t -> t.field("attrs.attrId").value(entry.getKey().toString()));
                var terms = QueryBuilders
                        .terms(t -> t.withJson(
                                new StringReader(String.format("{\"attrs.attrValue\":%s}", entry.getValue()))
                        ));
                return QueryBuilders.bool(b -> b.must(term, terms));
            }).toList();

            boolQueryBuilder.filter(f -> f.nested(n -> n.path("attrs").query(q -> q.bool(b -> b.should(queries)))));
        }

        if (param.getHasStock() != null) {
            boolQueryBuilder.filter(f -> f.term(t -> t.field("hasStock").value(param.getHasStock().equals(1))));
        }

        if (param.getSkuPrice() != null && !param.getSkuPrice().trim().isEmpty()) {
            List<BigDecimal> prices = Arrays.stream(param.getSkuPrice().split("_"))
                    .filter(s -> !s.isBlank() && s.matches("^\\d+(\\.\\d+)?$"))
                    .map(BigDecimal::new)
                    .toList();
            RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder().field("skuPrice");
            if (!prices.isEmpty()) {
                rangeQueryBuilder.gte(JsonData.of(prices.get(0)));
            }
            if (prices.size() > 1) {
                rangeQueryBuilder.lte(JsonData.of(prices.get(1)));
            }
            boolQueryBuilder.filter(q -> q.range(rangeQueryBuilder.build()));
        }

        // TODO: sort, from, size, aggs

        return builder.withQuery(q -> q.bool(boolQueryBuilder.build())).build();
    }

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

        // 1.构造查询条件
        Query query = this.buildQuery(param);


        return null;
    }
}

package edu.nchu.mall.services.search.utils;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.utils.Entry;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.http.HttpStatus;

import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import jakarta.json.stream.JsonParser;

public class QueryUtils {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductQuery {
        public static final String INDEX_NAME = "product";
        public static final int DEFAULT_PAGE_SIZE = 10;
        public static final int DEFAULT_PAGE_NUM = 0;
        public static final String DEFAULT_HIGHLIGHT_PRE_TAG = "<b style='color:red;'>";
        public static final String DEFAULT_HIGHLIGHT_POST_TAG = "</b>";
        private static final List<Entry<String, Aggregation>> AGGREGATIONS;

        static {
            try {
                ClassPathResource resource = new ClassPathResource("dsl/search/product.agg.json");
                byte[] bytes = Files.readAllBytes(resource.getFile().toPath());
                String agg_json = new String(bytes, StandardCharsets.UTF_8);

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(agg_json);
                JsonpMapper jsonpMapper = new JacksonJsonpMapper();

                List<Entry<String, Aggregation>> aggs = new LinkedList<>();
                Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    String aggName = entry.getKey(); // "brand_agg"
                    String aggBodyJson = entry.getValue().toString(); // "{ "terms": ... }"

                    try (StringReader reader = new StringReader(aggBodyJson);
                         JsonParser parser = jsonpMapper.jsonProvider().createParser(reader)) {
                        Aggregation aggregation = Aggregation._DESERIALIZER.deserialize(parser, jsonpMapper);
                        aggs.add(Entry.of(aggName, aggregation));
                    }
                }
                AGGREGATIONS = List.copyOf(aggs);

            } catch (Exception e) {
                throw new CustomException("解析聚合query失败", e, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        private int pageSize = DEFAULT_PAGE_SIZE;
        private int pageNum = DEFAULT_PAGE_NUM;
        private String highlightPreTag = DEFAULT_HIGHLIGHT_PRE_TAG;
        private String highlightPostTag = DEFAULT_HIGHLIGHT_POST_TAG;


        // src/main/resources/dsl/search/product.jsonl
        public Query buildQuery(ProductSearchParam param) throws Exception{

            NativeQueryBuilder builder = new NativeQueryBuilder();
            BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

            if (param.getKeyword() != null && !param.getKeyword().trim().isEmpty()) {
                builder.withHighlightQuery(
                        new HighlightQuery(
                                new Highlight(
                                        HighlightParameters.builder()
                                                .withPreTags(highlightPreTag)
                                                .withPostTags(highlightPostTag)
                                                .build()
                                        ,
                                        List.of(new HighlightField("skuTitle"))
                                ),
                                Product.class
                        )
                );
                boolQueryBuilder.must(q -> q.match(m -> m.field("skuTitle").query(param.getKeyword())));
            }

            if (param.getCatalogId() != null) {
                boolQueryBuilder.filter(q -> q.term(t -> t.field("catalogId").value(param.getCatalogId())));
            }

            if (param.getBrandIds() != null && !param.getBrandIds().isEmpty()) {
                String json = String.format("{\"brandId\":%s}", param.getBrandIds());
                boolQueryBuilder.filter(q -> q.terms(t -> t.withJson(new StringReader(json))));
            }

            // attrs=1_5寸:8寸&attrs=2_16G:4GB
            if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {
                Map<Long, List<String>> attrs = new HashMap<>();
                for (String attr : param.getAttrs()) {
                    String[] split = attr.split("_");
                    if (split.length != 2) continue;
                    var op = KeyUtils.parseKey2Long(split[0]);
                    if (op.isEmpty()) continue;
                    String[] vals = split[1].split(":");
                    if (attrs.containsKey(op.get())) {
                        attrs.get(op.get()).addAll(Arrays.asList(vals));
                    } else {
                        attrs.put(op.get(), new ArrayList<>(Arrays.asList(vals)));
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

            // sort, from, size, aggs

            // skuPrice_asc/desc
            if (param.getSort() != null) {
                List<String[]> sort = param.getSort().stream()
                        .map(s -> s.split("_"))
                        .filter(s -> s.length == 2)
                        .toList();
                List<SortOptions> sorts = sort.stream()
                        .map(
                                s -> SortOptionsBuilders.field(f -> f.field(s[0]).order(s[1].equals(SortOrder.Asc.toString()) ? SortOrder.Asc : SortOrder.Desc))
                        ).toList();
                builder.withSort(sorts);
            }

            int pageNum = param.getPageNum() == null ? DEFAULT_PAGE_NUM : param.getPageNum();
            int pageSize = param.getPageSize() == null ? DEFAULT_PAGE_SIZE : param.getPageSize();
            builder.withPageable(PageRequest.of(pageNum, pageSize));

            for (Entry<String, Aggregation> entry : AGGREGATIONS) {
                builder.withAggregation(entry.getKey(), entry.getValue());
            }

            BoolQuery boolQuery = boolQueryBuilder.build();
            return builder.withQuery(q -> q.bool(boolQuery)).build();
        }

    }
}

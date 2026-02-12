package edu.nchu.mall.services.search.utils;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.FieldValue;
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
        public static final String BRAND_AGG = "brand_agg";
        public static final String BRAND_NAME_AGG = "brandName_agg";
        public static final String BRAND_IMG_AGG = "brandImg_agg";
        public static final String CATALOG_AGG = "catalog_agg";
        public static final String CATALOG_NAME_AGG = "catalogName_agg";
        public static final String ATTR_AGG = "attr_agg";
        public static final String ATTR_ID_AGG = "attrId_agg";
        public static final String ATTR_NAME_AGG = "attrName_agg";
        public static final String ATTR_VALUE_AGG = "attrValue_agg";
        private static final List<Entry<String, Aggregation>> AGGREGATIONS;

        static {
            try {
                ClassPathResource resource = new ClassPathResource("dsl/search/product.agg.json");
                String agg_json;
                try (var inputStream = resource.getInputStream()) {
                    agg_json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }

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
                boolQueryBuilder.filter(q -> q.term(t -> t.field("catalogId").value(String.valueOf(param.getCatalogId()))));
            }

            if (param.getBrandIds() != null && !param.getBrandIds().isEmpty()) {
                List<FieldValue> brandValues = param.getBrandIds().stream()
                        .map(String::valueOf)
                        .map(FieldValue::of)
                        .toList();
                boolQueryBuilder.filter(q -> q.terms(t -> t
                        .field("brandId")
                        .terms(v -> v.value(brandValues))
                ));
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
                            .terms(t -> t.field("attrs.attrValue").terms(v -> v.value(entry.getValue().stream().map(FieldValue::of).toList())));
                    return QueryBuilders.bool(b -> b.must(term, terms));
                }).toList();

                boolQueryBuilder.filter(f -> f.nested(n -> n.path("attrs").query(q -> q.bool(b -> b.should(queries)))));
            }

            if (param.getHasStock() != null) {
                boolQueryBuilder.filter(f -> f.term(t -> t.field("hasStock").value(param.getHasStock().equals(1))));
            }

            if (param.getSkuPrice() != null && !param.getSkuPrice().trim().isEmpty()) {
                String[] parts = param.getSkuPrice().split("_", -1);
                BigDecimal minPrice = null;
                BigDecimal maxPrice = null;
                if (parts.length > 0) {
                    String left = parts[0].trim();
                    if (!left.isEmpty() && left.matches("^\\d+(\\.\\d+)?$")) {
                        minPrice = new BigDecimal(left);
                    }
                }
                if (parts.length > 1) {
                    String right = parts[1].trim();
                    if (!right.isEmpty() && right.matches("^\\d+(\\.\\d+)?$")) {
                        maxPrice = new BigDecimal(right);
                    }
                }
                RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder().field("skuPrice");
                if (minPrice != null) {
                    rangeQueryBuilder.gte(JsonData.of(minPrice));
                }
                if (maxPrice != null) {
                    rangeQueryBuilder.lte(JsonData.of(maxPrice));
                }
                if (minPrice != null || maxPrice != null) {
                    boolQueryBuilder.filter(q -> q.range(rangeQueryBuilder.build()));
                }
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

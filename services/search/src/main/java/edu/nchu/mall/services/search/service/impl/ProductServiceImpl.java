package edu.nchu.mall.services.search.service.impl;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import com.rabbitmq.client.Channel;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import edu.nchu.mall.services.search.repository.ProductRepository;
import edu.nchu.mall.services.search.service.ProductService;
import edu.nchu.mall.services.search.utils.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RabbitListener(queues = "product.spu.elastic")
public class ProductServiceImpl implements ProductService {
    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Autowired
    ProductRepository repository;

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object msg) {
        log.error("未知的消息类型: {}", msg);
    }

    @Override
    public void save(Product product) {
        repository.save(product);
    }

    @Override
    public void saveAll(Iterable<Product> products) {
        repository.saveAll(products);
    }

    @RabbitHandler
    public void saveAll(@Payload Iterable<Product> products, Channel channel, Message message) throws IOException {
        log.info("收到消息，写入Elasticsearch...");
        repository.saveAll(products);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
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
        Query query;
        QueryUtils.ProductQuery productQuery = new QueryUtils.ProductQuery();
        try{
            query = productQuery.buildQuery(param);
        }catch (Exception e){
            throw new CustomException("封装商品查询请求失败", e, HttpStatus.BAD_REQUEST);
        }

        ProductSearchResult res = new ProductSearchResult();
        SearchHits<Product> result = elasticsearchOperations.search(query, Product.class);
        res.setProducts(result.getSearchHits().stream().map(SearchHit::getContent).toList());
        res.setTotal(result.getTotalHits());
        int pageSize = param.getPageSize() == null ? QueryUtils.ProductQuery.DEFAULT_PAGE_SIZE : param.getPageSize();
        int pageNum = param.getPageNum() == null ? QueryUtils.ProductQuery.DEFAULT_PAGE_NUM : param.getPageNum();
        int pages = result.getTotalHits() == 0 ? 0 : (int) ((result.getTotalHits() + pageSize - 1) / pageSize);
        res.setPages(pages);
        res.setPageNum(pageNum);

        AggregationsContainer<?> aggregations = result.getAggregations();
        if (aggregations == null) return res;
        if (!(aggregations instanceof ElasticsearchAggregations elasticsearchAggregations)) return res;

        res.setBrands(parseBrandAggregations(elasticsearchAggregations));
        res.setCatalogs(parseCatalogAggregations(elasticsearchAggregations));
        res.setAttrs(parseAttrAggregations(elasticsearchAggregations));

        return res;
    }

    private List<ProductSearchResult.RelatedBrand> parseBrandAggregations(ElasticsearchAggregations aggregations) {
        Aggregate root = getAggregateByName(aggregations, QueryUtils.ProductQuery.BRAND_AGG);
        if (root == null) return Collections.emptyList();
        List<ProductSearchResult.RelatedBrand> brands = new ArrayList<>();
        if (root.isSterms()) {
            for (StringTermsBucket bucket : resolveBuckets(root.sterms().buckets())) {
                ProductSearchResult.RelatedBrand item = new ProductSearchResult.RelatedBrand();
                item.setBrandId(KeyUtils.parseKey2Long(extractStringKey(bucket)).orElse(null));
                item.setBrandName(extractFirstBucketKey(bucket.aggregations(), QueryUtils.ProductQuery.BRAND_NAME_AGG));
                item.setLogo(extractFirstBucketKey(bucket.aggregations(), QueryUtils.ProductQuery.BRAND_IMG_AGG));
                brands.add(item);
            }
        }
        if (root.isLterms()) {
            for (LongTermsBucket bucket : resolveBuckets(root.lterms().buckets())) {
                ProductSearchResult.RelatedBrand item = new ProductSearchResult.RelatedBrand();
                item.setBrandId(bucket.key());
                item.setBrandName(extractFirstBucketKey(bucket.aggregations(), QueryUtils.ProductQuery.BRAND_NAME_AGG));
                item.setLogo(extractFirstBucketKey(bucket.aggregations(), QueryUtils.ProductQuery.BRAND_IMG_AGG));
                brands.add(item);
            }
        }
        return brands;
    }

    private List<ProductSearchResult.RelatedCatalog> parseCatalogAggregations(ElasticsearchAggregations aggregations) {
        Aggregate root = getAggregateByName(aggregations, QueryUtils.ProductQuery.CATALOG_AGG);
        if (root == null) return Collections.emptyList();
        List<ProductSearchResult.RelatedCatalog> catalogs = new ArrayList<>();
        if (root.isSterms()) {
            for (StringTermsBucket bucket : resolveBuckets(root.sterms().buckets())) {
                ProductSearchResult.RelatedCatalog item = new ProductSearchResult.RelatedCatalog();
                item.setCatalogId(KeyUtils.parseKey2Long(extractStringKey(bucket)).orElse(null));
                item.setCatalogName(extractFirstBucketKey(bucket.aggregations(), QueryUtils.ProductQuery.CATALOG_NAME_AGG));
                catalogs.add(item);
            }
        }
        if (root.isLterms()) {
            for (LongTermsBucket bucket : resolveBuckets(root.lterms().buckets())) {
                ProductSearchResult.RelatedCatalog item = new ProductSearchResult.RelatedCatalog();
                item.setCatalogId(bucket.key());
                item.setCatalogName(extractFirstBucketKey(bucket.aggregations(), QueryUtils.ProductQuery.CATALOG_NAME_AGG));
                catalogs.add(item);
            }
        }
        return catalogs;
    }

    private List<ProductSearchResult.RelatedAttr> parseAttrAggregations(ElasticsearchAggregations aggregations) {
        Aggregate root = getAggregateByName(aggregations, QueryUtils.ProductQuery.ATTR_AGG);
        if (root == null) return Collections.emptyList();
        if (!root.isNested()) return Collections.emptyList();

        Aggregate attrIdAgg = root.nested().aggregations().get(QueryUtils.ProductQuery.ATTR_ID_AGG);
        if (attrIdAgg == null) return Collections.emptyList();

        List<ProductSearchResult.RelatedAttr> attrs = new ArrayList<>();
        if (attrIdAgg.isSterms()) {
            for (StringTermsBucket bucket : resolveBuckets(attrIdAgg.sterms().buckets())) {
                ProductSearchResult.RelatedAttr item = new ProductSearchResult.RelatedAttr();
                item.setAttrId(KeyUtils.parseKey2Long(extractStringKey(bucket)).orElse(null));
                item.setAttrName(extractFirstBucketKey(bucket.aggregations(), QueryUtils.ProductQuery.ATTR_NAME_AGG));
                item.setAttrValue(extractAllBucketKeys(bucket.aggregations(), QueryUtils.ProductQuery.ATTR_VALUE_AGG));
                attrs.add(item);
            }
        }
        if (attrIdAgg.isLterms()) {
            for (LongTermsBucket bucket : resolveBuckets(attrIdAgg.lterms().buckets())) {
                ProductSearchResult.RelatedAttr item = new ProductSearchResult.RelatedAttr();
                item.setAttrId(bucket.key());
                item.setAttrName(extractFirstBucketKey(bucket.aggregations(), QueryUtils.ProductQuery.ATTR_NAME_AGG));
                item.setAttrValue(extractAllBucketKeys(bucket.aggregations(), QueryUtils.ProductQuery.ATTR_VALUE_AGG));
                attrs.add(item);
            }
        }
        return attrs;
    }

    private Aggregate getAggregateByName(ElasticsearchAggregations aggregations, String aggName) {
        ElasticsearchAggregation aggregation = aggregations.get(aggName);
        return aggregation == null ? null : aggregation.aggregation().getAggregate();
    }

    private String extractStringKey(StringTermsBucket bucket) {
        if (bucket == null) return null;
        if (bucket.key().isString()) return bucket.key().stringValue();
        return String.valueOf(bucket.key()._get());
    }

    private String extractFirstBucketKey(Map<String, Aggregate> subAggs, String subAggName) {
        Aggregate sub = subAggs == null ? null : subAggs.get(subAggName);
        if (sub == null) return null;
        if (sub.isSterms()) {
            List<StringTermsBucket> buckets = resolveBuckets(sub.sterms().buckets());
            if (buckets.isEmpty()) return null;
            return extractStringKey(buckets.get(0));
        }
        if (sub.isLterms()) {
            List<LongTermsBucket> buckets = resolveBuckets(sub.lterms().buckets());
            if (buckets.isEmpty()) return null;
            return String.valueOf(buckets.get(0).key());
        }
        return null;
    }

    private List<String> extractAllBucketKeys(Map<String, Aggregate> subAggs, String subAggName) {
        Aggregate sub = subAggs == null ? null : subAggs.get(subAggName);
        if (sub == null) return Collections.emptyList();
        if (sub.isSterms()) {
            return resolveBuckets(sub.sterms().buckets()).stream()
                    .map(this::extractStringKey)
                    .toList();
        }
        if (sub.isLterms()) {
            return resolveBuckets(sub.lterms().buckets()).stream()
                    .map(LongTermsBucket::key)
                    .map(String::valueOf)
                    .toList();
        }
        return Collections.emptyList();
    }

    private <T> List<T> resolveBuckets(Buckets<T> buckets) {
        if (buckets == null) return Collections.emptyList();
        if (buckets.isArray()) return buckets.array();
        if (buckets.isKeyed()) return new ArrayList<>(buckets.keyed().values());
        return Collections.emptyList();
    }

}

package edu.nchu.mall.services.search.service.support;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import edu.nchu.mall.components.utils.KeyUtils;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import edu.nchu.mall.services.search.utils.QueryUtils;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ProductSearchMapper {

    public ProductSearchResult.ProductItem toProductItem(Product product) {
        ProductSearchResult.ProductItem item = new ProductSearchResult.ProductItem();
        item.setSpuId(parseLong(product.getSpuId()));
        item.setDefaultSkuId(parseLong(product.getDefaultSkuId()));
        item.setSpuName(product.getSpuName());
        item.setDefaultImage(product.getDefaultImage());
        item.setMinPrice(product.getMinPrice());
        item.setMaxPrice(product.getMaxPrice());
        item.setSaleCount(product.getSaleCount());
        item.setHasStock(product.getHasStock());
        item.setHotScore(product.getHotScore());
        item.setBrandId(parseLong(product.getBrandId()));
        item.setBrandName(product.getBrandName());
        item.setBrandImg(product.getBrandImg());
        item.setCatalogId(parseLong(product.getCatalogId()));
        item.setCatalogName(product.getCatalogName());
        return item;
    }

    public List<ProductSearchResult.RelatedBrand> parseBrandAggregations(ElasticsearchAggregations aggregations) {
        Aggregate root = getAggregateByName(aggregations, QueryUtils.ProductQuery.BRAND_AGG);
        if (root == null) {
            return Collections.emptyList();
        }

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

    public List<ProductSearchResult.RelatedCatalog> parseCatalogAggregations(ElasticsearchAggregations aggregations) {
        Aggregate root = getAggregateByName(aggregations, QueryUtils.ProductQuery.CATALOG_AGG);
        if (root == null) {
            return Collections.emptyList();
        }

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

    public List<ProductSearchResult.RelatedAttr> parseAttrAggregations(ElasticsearchAggregations aggregations) {
        Aggregate root = getAggregateByName(aggregations, QueryUtils.ProductQuery.ATTR_AGG);
        if (root == null || !root.isNested()) {
            return Collections.emptyList();
        }

        Aggregate attrIdAgg = root.nested().aggregations().get(QueryUtils.ProductQuery.ATTR_ID_AGG);
        if (attrIdAgg == null) {
            return Collections.emptyList();
        }

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
        if (bucket == null) {
            return null;
        }
        if (bucket.key().isString()) {
            return bucket.key().stringValue();
        }
        return String.valueOf(bucket.key()._get());
    }

    private String extractFirstBucketKey(Map<String, Aggregate> subAggs, String subAggName) {
        Aggregate sub = subAggs == null ? null : subAggs.get(subAggName);
        if (sub == null) {
            return null;
        }
        if (sub.isSterms()) {
            List<StringTermsBucket> buckets = resolveBuckets(sub.sterms().buckets());
            return buckets.isEmpty() ? null : extractStringKey(buckets.get(0));
        }
        if (sub.isLterms()) {
            List<LongTermsBucket> buckets = resolveBuckets(sub.lterms().buckets());
            return buckets.isEmpty() ? null : String.valueOf(buckets.get(0).key());
        }
        return null;
    }

    private List<String> extractAllBucketKeys(Map<String, Aggregate> subAggs, String subAggName) {
        Aggregate sub = subAggs == null ? null : subAggs.get(subAggName);
        if (sub == null) {
            return Collections.emptyList();
        }
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
        if (buckets == null) {
            return Collections.emptyList();
        }
        if (buckets.isArray()) {
            return buckets.array();
        }
        if (buckets.isKeyed()) {
            return new ArrayList<>(buckets.keyed().values());
        }
        return Collections.emptyList();
    }

    private Long parseLong(String value) {
        return KeyUtils.parseKey2Long(value).orElse(null);
    }
}

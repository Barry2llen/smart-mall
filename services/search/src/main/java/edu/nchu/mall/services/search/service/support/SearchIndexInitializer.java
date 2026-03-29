package edu.nchu.mall.services.search.service.support;

import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.utils.QueryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    public void ensureProductIndexReady() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(Product.class);
        if (indexOps.exists()) {
            return;
        }

        boolean created = indexOps.create();
        if (!created && !indexOps.exists()) {
            throw new CustomException("创建 Elasticsearch 索引失败: " + QueryUtils.ProductQuery.INDEX_NAME);
        }

        Document mapping = indexOps.createMapping(Product.class);
        boolean mapped = indexOps.putMapping(mapping);
        if (!mapped) {
            throw new CustomException("创建 Elasticsearch 映射失败: " + QueryUtils.ProductQuery.INDEX_NAME);
        }
        log.info("已创建 Elasticsearch 索引及映射 [{}]", QueryUtils.ProductQuery.INDEX_NAME);
    }
}

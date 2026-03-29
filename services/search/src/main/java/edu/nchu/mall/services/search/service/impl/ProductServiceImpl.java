package edu.nchu.mall.services.search.service.impl;

import com.rabbitmq.client.Channel;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import edu.nchu.mall.services.search.repository.ProductRepository;
import edu.nchu.mall.services.search.service.ProductService;
import edu.nchu.mall.services.search.service.support.ProductMessageConverter;
import edu.nchu.mall.services.search.service.support.ProductSearchMapper;
import edu.nchu.mall.services.search.service.support.SearchIndexInitializer;
import edu.nchu.mall.services.search.utils.QueryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RabbitListener(queues = "product.spu.elastic.queue")
public class ProductServiceImpl implements ProductService {
    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Autowired
    ProductRepository repository;

    @Autowired
    SearchIndexInitializer searchIndexInitializer;

    @Autowired
    ProductMessageConverter productMessageConverter;

    @Autowired
    ProductSearchMapper productSearchMapper;

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object msg) {
        log.error("未知的消息类型: {}", msg);
    }

    @Override
    public void save(Product product) {
        searchIndexInitializer.ensureProductIndexReady();
        Product normalized = productMessageConverter.toProduct(product);
        if (normalized != null) {
            repository.save(normalized);
        }
    }

    @Override
    public void saveAll(Iterable<Product> products) {
        searchIndexInitializer.ensureProductIndexReady();
        repository.saveAll(productMessageConverter.toProducts(products));
    }

    @RabbitHandler
    public void saveAll(@Payload Object products, Channel channel, Message message) throws IOException {
        log.info("收到消息，写入Elasticsearch...");
        searchIndexInitializer.ensureProductIndexReady();
        repository.saveAll(productMessageConverter.toProducts(products));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitHandler
    public void deleteById(@Payload Long spuId, Channel channel, Message message) throws IOException {
        log.info("收到删除消息，删除 Elasticsearch 中的 spu 文档 [spuId={}]", spuId);
        repository.deleteById(String.valueOf(spuId));
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
        searchIndexInitializer.ensureProductIndexReady();
        Query query;
        QueryUtils.ProductQuery productQuery = new QueryUtils.ProductQuery();
        try{
            query = productQuery.buildQuery(param);
        }catch (Exception e){
            throw new CustomException("封装商品查询请求失败", e, HttpStatus.BAD_REQUEST);
        }

        ProductSearchResult res = new ProductSearchResult();
        SearchHits<Product> result;
        try {
            result = elasticsearchOperations.search(query, Product.class);
        } catch (Exception e) {
            log.error("商品搜索失败 [index={}, keyword={}, catalogId={}, brandIds={}, attrs={}, hasStock={}, skuPrice={}, sort={}]",
                    QueryUtils.ProductQuery.INDEX_NAME,
                    param.getKeyword(),
                    param.getCatalogId(),
                    param.getBrandIds(),
                    param.getAttrs(),
                    param.getHasStock(),
                    param.getSkuPrice(),
                    param.getSort(),
                    e);
            throw e;
        }
        res.setProducts(result.getSearchHits().stream().map(SearchHit::getContent).map(productSearchMapper::toProductItem).toList());
        res.setTotal(result.getTotalHits());
        int pageSize = param.getPageSize() == null ? QueryUtils.ProductQuery.DEFAULT_PAGE_SIZE : param.getPageSize();
        int pageNum = param.getPageNum() == null ? QueryUtils.ProductQuery.DEFAULT_PAGE_NUM : param.getPageNum();
        int pages = result.getTotalHits() == 0 ? 0 : (int) ((result.getTotalHits() + pageSize - 1) / pageSize);
        res.setPages(pages);
        res.setPageNum(pageNum);

        AggregationsContainer<?> aggregations = result.getAggregations();
        if (aggregations == null) return res;
        if (!(aggregations instanceof ElasticsearchAggregations elasticsearchAggregations)) return res;

        res.setBrands(productSearchMapper.parseBrandAggregations(elasticsearchAggregations));
        res.setCatalogs(productSearchMapper.parseCatalogAggregations(elasticsearchAggregations));
        res.setAttrs(productSearchMapper.parseAttrAggregations(elasticsearchAggregations));

        return res;
    }
}

package edu.nchu.mall.services.search.repository;

import edu.nchu.mall.services.search.document.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
}

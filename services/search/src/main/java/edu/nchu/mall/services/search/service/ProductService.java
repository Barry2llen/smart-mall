package edu.nchu.mall.services.search.service;

import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.dto.ProductSearchParam;
import edu.nchu.mall.services.search.dto.ProductSearchResult;
import jakarta.validation.Valid;

public interface ProductService {
    void save(Product product);

    void saveAll(Iterable<Product> products);

    void deleteById(String id);

    void deleteAll(Iterable<String> ids);

    ProductSearchResult search(ProductSearchParam param);
}

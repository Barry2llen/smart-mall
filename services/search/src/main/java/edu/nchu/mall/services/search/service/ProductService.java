package edu.nchu.mall.services.search.service;

import edu.nchu.mall.services.search.document.Product;

public interface ProductService {
    void save(Product product);

    void saveAll(Iterable<Product> products);

    void deleteById(String id);

    void deleteAll(Iterable<String> ids);
}

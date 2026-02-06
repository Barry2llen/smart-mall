package edu.nchu.mall.services.search.service;

import edu.nchu.mall.services.search.document.Product;

import java.util.Collection;

public interface ProductService {
    void save(Product product);

    void saveAll(Collection<Product> products);
}

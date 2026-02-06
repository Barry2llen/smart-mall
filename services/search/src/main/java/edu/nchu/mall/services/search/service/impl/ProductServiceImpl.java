package edu.nchu.mall.services.search.service.impl;

import edu.nchu.mall.services.search.exception.EsOperationException;
import edu.nchu.mall.services.search.document.Product;
import edu.nchu.mall.services.search.repository.ProductRepository;
import edu.nchu.mall.services.search.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository repository;

    @Override
    public void save(Product product) {
        repository.save(product);
    }

    @Override
    public void saveAll(Collection<Product> products) {
        repository.saveAll(products);
    }
}

package edu.nchu.mall.services.search.service.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.services.search.document.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductMessageConverter {

    private final ObjectMapper objectMapper;

    public List<Product> toProducts(Object payload) {
        if (payload == null) {
            return List.of();
        }
        if (payload instanceof Iterable<?> iterable) {
            List<Product> products = new ArrayList<>();
            for (Object item : iterable) {
                Product product = toProduct(item);
                if (product != null) {
                    products.add(product);
                }
            }
            return products;
        }

        Product product = toProduct(payload);
        return product == null ? List.of() : List.of(product);
    }

    public Product toProduct(Object payload) {
        if (payload == null) {
            return null;
        }
        Product product;
        if (payload instanceof Product current) {
            product = current;
        } else if (payload instanceof Map<?, ?> map) {
            product = objectMapper.convertValue(map, Product.class);
        } else {
            product = objectMapper.convertValue(payload, Product.class);
        }

        if (product.getId() == null && product.getSpuId() != null) {
            product.setId(product.getSpuId());
        }
        return product;
    }
}

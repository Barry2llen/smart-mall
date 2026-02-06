package edu.nchu.mall.components.feign.search;

import edu.nchu.mall.models.annotation.NotNullCollection;
import edu.nchu.mall.models.document.EsProduct;
import edu.nchu.mall.models.model.R;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("search")
public interface SearchFeignClient {
    @PostMapping("/product")
    R<?> saveProduct(@RequestBody EsProduct body);

    @PostMapping("/product/bulk")
    R<?> saveProductAll(@RequestBody @Valid @NotNullCollection List<EsProduct> products);
}

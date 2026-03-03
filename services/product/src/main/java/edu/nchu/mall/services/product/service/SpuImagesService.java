package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SpuImages;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface SpuImagesService extends IService<SpuImages> {
    Map<Long, String> getSpuDefaultImagesBatch(Collection<Long> spuIds);
}

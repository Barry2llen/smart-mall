package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.Attr;

import java.io.Serializable;

public interface AttrService extends IService<Attr> {
    @Override
    boolean updateById(Attr entity);

    @Override
    Attr getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}

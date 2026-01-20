package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.SpuComment;

import java.io.Serializable;

public interface SpuCommentService extends IService<SpuComment> {
    @Override
    boolean updateById(SpuComment entity);

    @Override
    SpuComment getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}

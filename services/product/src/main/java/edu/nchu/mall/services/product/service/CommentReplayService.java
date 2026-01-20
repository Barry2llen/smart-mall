package edu.nchu.mall.services.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.CommentReplay;

import java.io.Serializable;

public interface CommentReplayService extends IService<CommentReplay> {
    @Override
    boolean updateById(CommentReplay entity);

    @Override
    CommentReplay getById(Serializable id);

    @Override
    boolean removeById(Serializable id);
}

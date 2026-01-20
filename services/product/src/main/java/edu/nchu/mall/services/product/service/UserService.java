package edu.nchu.mall.services.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.nchu.mall.models.entity.User;

import java.io.Serializable;

public interface UserService extends IService<User> {
    @Override
    boolean updateById(User entity);

    @Override
    User getById(Serializable id);

    @Override
    boolean removeById(Serializable id);


}

package edu.nchu.mall.services.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.nchu.mall.services.product.dao.UserMapper;
import edu.nchu.mall.services.product.entity.User;
import edu.nchu.mall.services.product.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public boolean saveUser(User user){
        return this.save(user);
    }

    @Override
    public boolean updateUser(User user) {
        return this.updateById(user);
    }

    @Override
    public User user(Long id) {
        return this.getById(id);
    }

    @Override
    public List<User> users() {
        return this.list();
    }
}

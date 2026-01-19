package edu.nchu.mall.services.product.service;

import edu.nchu.mall.services.product.entity.User;
import java.util.List;

public interface UserService {
    boolean saveUser(User user);

    boolean updateUser(User user);

    User user(Long id);

    List<User> users();
}

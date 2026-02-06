package edu.nchu.mall.services.search.service.impl;

import edu.nchu.mall.services.search.document.User;
import edu.nchu.mall.services.search.repository.UserRepository;
import edu.nchu.mall.services.search.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean save(User user) {
        User res = userRepository.save(user);
        return res.getId() != null;
    }
}

package edu.nchu.mall.services.search.repository;

import edu.nchu.mall.services.search.document.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserRepository extends ElasticsearchRepository<User, String> {
    List<User> findByNameContaining(String name);

    List<User> findByNameAndAge(String name, Integer age);

    List<User> findByEmail(String email);
}

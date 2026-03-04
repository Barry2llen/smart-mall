package edu.nchu.mall.components.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Component
public class RedisUtils {

    private static final DefaultRedisScript<Long> VALIDATE_AND_DELETE_SCRIPT = createValidateAndDeleteScript();

    @Autowired
    StringRedisTemplate redisTemplate;

    public Long sadd(String key, String... members) {
        return redisTemplate.opsForSet().add(key, members);
    }

    public Set<String> smembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Boolean unlink(String key) {
        return redisTemplate.unlink(key);
    }

    public Long unlink(Collection<String> keys) {
        return redisTemplate.unlink(keys);
    }

    public boolean checkAndDelete(String key, String code) {
        Long result = redisTemplate.execute(
                VALIDATE_AND_DELETE_SCRIPT,
                Collections.singletonList(key),
                code
        );
        return Long.valueOf(1L).equals(result);
    }

    private static DefaultRedisScript<Long> createValidateAndDeleteScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
                """
                        local key = KEYS[1]
                        local inputCode = ARGV[1]
                        local storedCode = redis.call('get', key)
                        if not storedCode then
                            return 0
                        end
                        if storedCode == inputCode then
                            redis.call('del', key)
                            return 1
                        end
                        return -1"""
        );
        return script;
    }
}

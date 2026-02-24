package edu.nchu.mall.services.cart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.nchu.mall.components.config.ThreadPoolConfig;
import edu.nchu.mall.components.exception.CustomException;
import edu.nchu.mall.components.feign.product.ProductFeignClient;
import edu.nchu.mall.components.feign.ware.WareFeignClient;
import edu.nchu.mall.components.utils.CallTaskUtils;
import edu.nchu.mall.models.model.R;
import edu.nchu.mall.models.model.RCT;
import edu.nchu.mall.models.model.Try;
import edu.nchu.mall.models.vo.SkuInfoVO;
import edu.nchu.mall.models.vo.SkuStockVO;
import edu.nchu.mall.services.cart.constants.RedisConstant;
import edu.nchu.mall.services.cart.dto.CartItemDTO;
import edu.nchu.mall.services.cart.entity.CartItem;
import edu.nchu.mall.services.cart.properties.CartConfigurationProperties;
import edu.nchu.mall.services.cart.service.CartService;
import edu.nchu.mall.models.vo.Cart;
import edu.nchu.mall.models.vo.CartItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
@CacheConfig(cacheNames = "cart")
public class CartServiceImpl implements CartService {

    @Autowired
    CallTaskUtils callTaskUtils;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Autowired
    CartConfigurationProperties cartConfigurationProperties;

    @Autowired
    ObjectMapper mapper;

    @Override
    public List<CartItemVO> getCartItems(Long userId) {
        return this.getCart(userId).getItems();
    }

    @Override
    @Cacheable(key = "T(edu.nchu.mall.services.cart.constants.RedisConstant).MICRO_CACHE_MARK + #userId", sync = true)
    public Cart getCart(Long userId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(RedisConstant.CART_KEY_PREFIX + userId);

        if (entries.isEmpty()) {
            return new Cart();
        }

        List<CartItem> items = entries.values().stream().map(v -> {
            try {
                return mapper.readValue(v.toString(), CartItem.class);
            } catch (JsonProcessingException e) {
                throw new CustomException("无法反序列化购物项");
            }
        }).sorted(Comparator.comparing(CartItem::getTime)).toList();

        List<Long> ids = items.stream().map(CartItem::getSkuId).toList();

        var voTask = callTaskUtils.rcall(() -> productFeignClient.getBatch(ids));

        var attrTask = callTaskUtils.rcall(() -> productFeignClient.getBatchSkuAttrValues(ids));

        var stockTask = callTaskUtils.rcall(() -> wareFeignClient.getStocksBySkuIds(ids));

        if (!Try.allSucceeded(voTask.join(), attrTask.join())) {
            throw new CustomException("获取商品信息失败");
        }

        Map<Long, SkuInfoVO> infos = voTask.join().getValue();
        Map<Long, List<String>> attrs = attrTask.join().getValue();
        Map<Long, SkuStockVO> stocks = stockTask.join().getValue().stream().collect(Collectors.toMap(SkuStockVO::getSkuId, v -> v));

        if (infos.size() != items.size()) {
            throw new CustomException("获取商品信息失败");
        }

        List<CartItemVO> vos = items.stream().filter(item -> stocks.containsKey(item.getSkuId())).map(item -> {
            CartItemVO vo = new CartItemVO();
            BeanUtils.copyProperties(item, vo);
            SkuInfoVO info = infos.get(item.getSkuId());
            vo.setSkuId(info.getSkuId());
            vo.setSpuId(info.getSpuId());
            vo.setTitle(info.getSkuTitle());
            vo.setImage(info.getSkuDefaultImg());
            vo.setPrice(info.getPrice());
            vo.setStock(stocks.get(item.getSkuId()).getAvailableStock());
            vo.setSkuAttr(attrs.get(info.getSkuId()));
            return vo;
        }).toList();

        Cart cart = new Cart();
        cart.setItems(vos);
        return cart;
    }

    @Override
    public Status addCartItem(Long userId, CartItemDTO dto) {
        final String key = RedisConstant.CART_KEY_PREFIX + userId;
        final String hashKey = String.valueOf(dto.getSkuId());

        Long size = redisTemplate.opsForHash().size(key);
        if (size >= cartConfigurationProperties.getMaxSize()) {
            return Status.CART_FULL;
        }

        Object obj = redisTemplate.opsForHash().get(key, hashKey);
        if (obj != null) {
            if (!(obj instanceof String json)) {
                return Status.ERROR;
            }

            CartItem cartItem = null;
            try {
                cartItem = mapper.readValue(json, CartItem.class);
            } catch (JsonProcessingException e) {
                return Status.ERROR;
            }
            cartItem.setCount(cartItem.getCount() + dto.getCount());
            cartItem.setTime(LocalDateTime.now());

            try {
                redisTemplate.opsForHash().put(key, hashKey, mapper.writeValueAsString(cartItem));
            } catch (JsonProcessingException e) {
                return Status.ERROR;
            }

            return Status.SUCCESS;
        }

        var existTask = callTaskUtils.rcall(() -> productFeignClient.getSkuInfo(String.valueOf(dto.getSkuId())));

        var stockTask = callTaskUtils.rcall(() -> wareFeignClient.getStockBySkuId(dto.getSkuId()));

        CompletableFuture.allOf(existTask, stockTask).join();

        var existTry = existTask.join();
        var stockTry = stockTask.join();

        if (!Try.allSucceeded(existTry, stockTry)) {
            return Status.ERROR;
        }

        SkuInfoVO info = existTry.getValue();
        SkuStockVO stock = stockTry.getValue();

        if (info == null || info.getSkuId() == null || !info.getSkuId().equals(dto.getSkuId())) {
            return Status.ERROR;
        }

        if (stock == null || stock.getAvailableStock() < dto.getCount()) {
            return Status.SKU_NOT_ENOUGH;
        }

        CartItem cartItem = new CartItem();
        cartItem.setSkuId(dto.getSkuId());
        cartItem.setCount(dto.getCount());
        cartItem.setTime(LocalDateTime.now());
        try {
            redisTemplate.opsForHash().put(key, hashKey, mapper.writeValueAsString(cartItem));
        } catch (JsonProcessingException e) {
            log.error("序列化购物项失败", e);
            return Status.ERROR;
        }
        return Status.SUCCESS;
    }

    @Override
    public Status deleteCartItem(Long userId, Long skuId) {
        final String key = RedisConstant.CART_KEY_PREFIX + userId;
        final String hashKey = String.valueOf(skuId);

        Boolean existed = redisTemplate.opsForHash().hasKey(key, hashKey);
        if (!existed) {
            return Status.CART_ITEM_NOT_FOUND;
        }

        Long removed = redisTemplate.opsForHash().delete(key, hashKey);
        return removed > 0 ? Status.SUCCESS : Status.ERROR;
    }

    @Override
    public Status checkCartItem(Long userId, Long skuId) {
        return updateCartItemSelection(userId, skuId, true);
    }

    @Override
    public Status uncheckCartItem(Long userId, Long skuId) {
        return updateCartItemSelection(userId, skuId, false);
    }

    @Override
    public Status updateCartItemCount(Long userId, Long skuId, Integer count) {
        final String key = RedisConstant.CART_KEY_PREFIX + userId;
        final String hashKey = String.valueOf(skuId);

        Object obj = redisTemplate.opsForHash().get(key, hashKey);
        if (obj == null) {
            return Status.CART_ITEM_NOT_FOUND;
        }
        if (!(obj instanceof String json)) {
            return Status.ERROR;
        }

        CartItem cartItem;
        try {
            cartItem = mapper.readValue(json, CartItem.class);
        } catch (JsonProcessingException e) {
            return Status.ERROR;
        }

        cartItem.setCount(count);
        //cartItem.setTime(LocalDateTime.now());

        try {
            redisTemplate.opsForHash().put(key, hashKey, mapper.writeValueAsString(cartItem));
        } catch (JsonProcessingException e) {
            return Status.ERROR;
        }
        return Status.SUCCESS;
    }

    private Status updateCartItemSelection(Long userId, Long skuId, boolean selected) {
        final String key = RedisConstant.CART_KEY_PREFIX + userId;
        final String hashKey = String.valueOf(skuId);

        Object obj = redisTemplate.opsForHash().get(key, hashKey);
        if (obj == null) {
            return Status.CART_ITEM_NOT_FOUND;
        }
        if (!(obj instanceof String json)) {
            return Status.ERROR;
        }

        CartItem cartItem;
        try {
            cartItem = mapper.readValue(json, CartItem.class);
        } catch (JsonProcessingException e) {
            return Status.ERROR;
        }

        cartItem.setSelected(selected);
        //cartItem.setTime(LocalDateTime.now());

        try {
            redisTemplate.opsForHash().put(key, hashKey, mapper.writeValueAsString(cartItem));
        } catch (JsonProcessingException e) {
            return Status.ERROR;
        }
        return Status.SUCCESS;
    }
}

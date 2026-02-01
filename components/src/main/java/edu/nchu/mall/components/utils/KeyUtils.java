package edu.nchu.mall.components.utils;

import java.util.Optional;

public class KeyUtils {
    public static Optional<Long> parseKey2Long(String key) {
        try{
            return Optional.of(Long.parseLong(key));
        }catch (NumberFormatException e){
            return Optional.empty();
        }
    }

    public static boolean isEmpty(String key) {
        return key == null || key.isEmpty();
    }
}

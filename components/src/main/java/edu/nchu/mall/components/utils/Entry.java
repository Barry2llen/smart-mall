package edu.nchu.mall.components.utils;

import java.util.Map;

public class Entry<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    public static <K, V> Entry<K, V> of(K key, V value) {
        return new Entry<>(key, value);
    }

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    public K setKey(K key) {
        this.key = key;
        return key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V v) {
        this.value = v;
        return v;
    }
}

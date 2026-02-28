package edu.nchu.mall.models.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WareOrderLockStatus implements IEnum<Integer> {
    LOCKED(1),
    UNLOCKED(2),
    DELETED(3);

    private final int value;

    WareOrderLockStatus(int value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public Integer getValue() {
        return value;
    }
}

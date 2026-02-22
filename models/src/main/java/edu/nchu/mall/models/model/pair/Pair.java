package edu.nchu.mall.models.model.pair;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pair <T, U> implements Serializable {
    private T first;
    private U second;

    public static <T, U> ImmutablePair<T, U> of(T first, U second) {
        return new ImmutablePair<>(first, second);
    }

    public static class ImmutablePair<T, U> extends Pair<T, U> {
        public ImmutablePair(T first, U second) {
            super(first, second);
        }

        @Override
        public void setFirst(T first) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSecond(U second) {
            throw new UnsupportedOperationException();
        }
    }
}

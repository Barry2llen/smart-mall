package edu.nchu.mall.models.model;

import edu.nchu.mall.models.model.pair.Pair;

import java.util.Collection;
import java.util.function.Function;

public class Try <T> extends Pair.ImmutablePair<T, Throwable> {

    public Try(T value, Throwable ex) {
        super(value, ex);
    }

    public Throwable getEx() {
        return super.getSecond();
    }

    public T getValue() {
        return super.getFirst();
    }

    public boolean succeeded() {
        return getEx() == null;
    }

    public boolean failed() {
        return getEx() != null;
    }

    public static <T> Try<T> success(T value) {
        return new Try<>(value, null);
    }

    public static <T> Try<T> failure(Throwable ex) {
        return new Try<>(null, ex);
    }

    public static boolean all(Function<Try<?>, Boolean> predicate, Try<?>... tries) {
        for (Try<?> try_ : tries) {
            if (!predicate.apply(try_)) {
                return false;
            }
        }
        return true;
    }

    public static boolean all(Function<Try<?>, Boolean> predicate, Collection<Try<?>> tries) {
        for (Try<?> try_ : tries) {
            if (!predicate.apply(try_)) {
                return false;
            }
        }
        return true;
    }

    public static boolean any(Function<Try<?>, Boolean> predicate, Try<?>... tries) {
        for (Try<?> try_ : tries) {
            if (predicate.apply(try_)) {
                return true;
            }
        }
        return false;
    }

    public static boolean any(Function<Try<?>, Boolean> predicate, Collection<Try<?>> tries) {
        for (Try<?> try_ : tries) {
            if (predicate.apply(try_)) {
                return true;
            }
        }
        return false;
    }

    public static boolean allSucceeded(Try<?>... tries) {
        return all(Try::succeeded, tries);
    }

    public static boolean allSucceeded(Collection<Try<?>> tries) {
        return all(Try::succeeded, tries);
    }
}

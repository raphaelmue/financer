package org.financer.util.collections;

import java.util.ArrayList;
import java.util.List;

public class Iterables {

    private Iterables() {

    }

    /**
     * Parses an iterable to a list.
     *
     * @param iterable iterable to parse
     * @param <T>      generic type of iterable
     * @return list
     */
    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> target = new ArrayList<>();
        iterable.forEach(target::add);
        return target;
    }

}

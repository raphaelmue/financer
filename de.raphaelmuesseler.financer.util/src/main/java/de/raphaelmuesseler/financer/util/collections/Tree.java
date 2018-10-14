package de.raphaelmuesseler.financer.util.collections;

import java.util.List;

public interface Tree<T> {
    T getCategory();

    Tree<T> getParent();

    List<? extends Tree<T>> getChildren();

    default boolean isLeaf() {
        return (getChildren().size() == 0);
    }

    default boolean isRoot() {
        return (getParent() == null);
    }
}

package de.raphaelmuesseler.financer.util.collections;

import java.util.List;

public interface Tree<T> {
    T getValue();

    Tree<T> getParent();
    void setParent(Tree<T> parent);

    List<? extends Tree<T>> getChildren();

    default boolean isLeaf() {
        return !getChildren().isEmpty();
    }

    default boolean isRoot() {
        return (getParent() == null);
    }
}

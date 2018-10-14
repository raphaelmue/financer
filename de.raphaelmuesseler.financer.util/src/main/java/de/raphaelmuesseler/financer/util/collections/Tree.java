package de.raphaelmuesseler.financer.util.collections;

import java.util.List;

public interface Tree<T> {
    T getValue();

    Tree<T> getParent();
    void setParent(Tree<T> parent);

    List<Tree<T>> getChildren();

    default boolean isLeaf() {
        return (getChildren().size() == 0);
    }

    default boolean isRoot() {
        return (getParent() == null);
    }
}

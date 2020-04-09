package org.financer.util.collections;

import java.util.Set;

public interface Tree {

    Tree getParent();

    Tree setParent(Tree parent);

    Set<? extends Tree> getChildren();

    default boolean isLeaf() {
        return getChildren() == null || getChildren().isEmpty();
    }

    default boolean isRoot() {
        return (getParent() == null);
    }
}

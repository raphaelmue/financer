package org.financer.util.collections;

import java.util.Set;

public interface Tree {

    interface Traversable<T extends Tree> {
        void onEnter(T tree);
    }

    Tree getParent();

    Tree setParent(Tree parent);

    Set<? extends Tree> getChildren();

    default boolean isLeaf() {
        return getChildren() == null || getChildren().isEmpty();
    }

    default boolean isRoot() {
        return (getParent() == null);
    }

    default <T extends Tree >void traverse(Traversable<T> traversable) {
        for (Tree child : this.getChildren()) {
            traversable.onEnter((T) child);
            if (!this.isLeaf()) {
                child.traverse(traversable);
            }
        }
    }
}

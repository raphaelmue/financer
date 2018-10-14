package de.raphaelmuesseler.financer.util.collections;

import java.util.Comparator;

public class TreeUtil {
    public static <T> boolean insertByValue(Tree<T> root, Tree<T> treeItem, Comparator<T> comparator) {
        for (Tree<T> item : root.getChildren()) {
            if (comparator.compare(treeItem.getCategory(), item.getCategory()) == 0) {
                item.getChildren().add(treeItem);
                return true;
            } else {
                if (TreeUtil.insertByValue(item, treeItem, comparator)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> boolean deleteByValue(Tree<T> root, Tree<T> treeItem, Comparator<T> comparator) {
        for (Tree<T> item : root.getChildren()) {
            if (comparator.compare(treeItem.getCategory(), item.getCategory()) == 0) {
                item.getChildren().remove(treeItem);
                return true;
            } else {
                if (TreeUtil.deleteByValue(item, treeItem, comparator)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> Tree<T> getByValue(Tree<T> root, Tree<T> treeItem, Comparator<T> comparator) {
        for (Tree<T> item : root.getChildren()) {
            if (comparator.compare(treeItem.getCategory(), item.getCategory()) == 0) {
                return item;
            } else {
                return TreeUtil.getByValue(item, treeItem, comparator);
            }
        }
        return null;
    }

    public static <T> void traverse(Tree<T> root, Action<Tree<T>> action) {
        action.action(root);
        if (!root.isLeaf()) {
            for (Tree<T> item : root.getChildren()) {
                TreeUtil.traverse(item, action);
            }
        }
    }

    public static <T> void traverseValue(Tree<T> root, Action<T> action) {
        action.action(root.getCategory());
        if (!root.isLeaf()) {
            for (Tree<T> item : root.getChildren()) {
                TreeUtil.traverseValue(item, action);
            }
        }
    }
}

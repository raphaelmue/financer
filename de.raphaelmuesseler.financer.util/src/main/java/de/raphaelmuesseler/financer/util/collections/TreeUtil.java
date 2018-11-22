package de.raphaelmuesseler.financer.util.collections;

import java.util.Comparator;
import java.util.List;

public class TreeUtil {
    public static <T> boolean insertByValue(Tree<T> root, Tree<T> treeItem, Comparator<T> comparator) {
        for (Tree<T> item : root.getChildren()) {
            if (comparator.compare(treeItem.getValue(), item.getValue()) == 0) {
                treeItem.setParent(root);
                ((List<Tree<T>>)item.getChildren()).add(treeItem);
                return true;
            } else {
                if (TreeUtil.insertByValue(item, treeItem, comparator)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> boolean insertByValue(List<? extends Tree<T>> roots, Tree<T> treeItem, Comparator<T> comparator) {
        for (Tree<T> root : roots) {
            if (insertByValue(root, treeItem, comparator)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean deleteByValue(Tree<T> root, Tree<T> treeItem, Comparator<T> comparator) {
        for (Tree<T> item : root.getChildren()) {
            if (comparator.compare(treeItem.getValue(), item.getValue()) == 0) {
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
        if (root != null) {
            for (Tree<T> item : root.getChildren()) {
                if (comparator.compare(treeItem.getValue(), item.getValue()) == 0) {
                    return item;
                } else {
                    if (TreeUtil.getByValue(item, treeItem, comparator) != null) {
                        return TreeUtil.getByValue(item, treeItem, comparator);
                    }
                }
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
        action.action(root.getValue());
        if (!root.isLeaf()) {
            for (Tree<T> item : root.getChildren()) {
                TreeUtil.traverseValue(item, action);
            }
        }
    }

    public static <T> void numberItemsByValue(Tree<T> root, NumberAction<Tree<T>> action) {
        TreeUtil.numberItemsByValue(root, action, "");
    }

    private static <T> void numberItemsByValue(Tree<T> root, NumberAction<Tree<T>> action, String prefix) {
        int counter = 1;
        if (!root.isLeaf()) {
            for (Tree<T> item : root.getChildren()) {
                String prefixCopy = prefix + counter + ".";
                action.action(item, prefixCopy);
                TreeUtil.numberItemsByValue(item, action, prefixCopy);
                counter++;
            }
        }
    }
}

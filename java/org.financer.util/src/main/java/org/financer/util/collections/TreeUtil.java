package org.financer.util.collections;

import java.util.Comparator;
import java.util.List;

public class TreeUtil {
    private TreeUtil() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static boolean insertByValue(Tree root, Tree treeItem, Comparator comparator) {
        for (Tree item : root.getChildren()) {
            if (comparator.compare(treeItem, item) == 0) {
                treeItem.setParent(root);
                ((List<Tree>) item.getChildren()).add(treeItem);
                return true;
            } else {
                if (TreeUtil.insertByValue(item, treeItem, comparator)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean insertByValue(List<? extends Tree> roots, Tree treeItem, Comparator comparator) {
        for (Tree root : roots) {
            if (insertByValue(root, treeItem, comparator)) {
                return true;
            }
        }
        return false;
    }

    public static boolean deleteByValue(Tree root, Tree treeItem, Comparator comparator) {
        for (Tree item : root.getChildren()) {
            if (comparator.compare(treeItem, item) == 0) {
                item.getParent().getChildren().remove(treeItem);
                return true;
            } else {
                if (TreeUtil.deleteByValue(item, treeItem, comparator)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Tree getByValue(Tree root, Tree treeItem, Comparator comparator) {
        if (root != null) {
            for (Tree item : root.getChildren()) {
                if (comparator.compare(treeItem, item) == 0) {
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

    public static void traverse(Tree root, Action<Tree> action) {
        action.action(root);
        if (!root.isLeaf()) {
            for (Tree item : root.getChildren()) {
                TreeUtil.traverse(item, action);
            }
        }
    }

    public static void traverseValue(Tree root, Action action) {
        action.action(root);
        if (!root.isLeaf()) {
            for (Tree item : root.getChildren()) {
                TreeUtil.traverseValue(item, action);
            }
        }
    }

    public static void numberItemsByValue(Tree root, NumberAction<Tree> action) {
        TreeUtil.numberItemsByValue(root, action, "");
    }

    private static void numberItemsByValue(Tree root, NumberAction<Tree> action, String prefix) {
        int counter = 1;
        if (!root.isLeaf()) {
            for (Tree item : root.getChildren()) {
                String prefixCopy = prefix + counter + ".";
                action.action(item, prefixCopy);
                TreeUtil.numberItemsByValue(item, action, prefixCopy);
                counter++;
            }
        }
    }
}

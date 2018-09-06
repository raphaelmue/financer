package de.raphaelmuesseler.financer.shared.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.TreeItem;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Iterator;

public class SerialTreeItem<T> extends TreeItem<T> implements Serializable {
    private static final long serialVersionUID = 6299083230638906835L;

    public SerialTreeItem(T value) {
        super(value);
    }

    public boolean isLeaf() {
        return (this.getChildren().size() == 0);
    }

    public JSONObject getJson() {
        return this.getJson(true);
    }

    private JSONObject getJson(boolean isRoot) {
        JSONObject result = new JSONObject();
        Gson gson = new GsonBuilder().create();
        for (TreeItem<T> item : this.getChildren()) {
            if (item != null) {
                SerialTreeItem<T> serialTreeItem = (SerialTreeItem<T>) item;
                if (serialTreeItem.getChildren().size() > 0) {
                    result.put(gson.toJson(serialTreeItem.getValue()), serialTreeItem.getJson(false));
                } else {
                    result.put(gson.toJson(serialTreeItem.getValue()), new JSONObject());
                }
            }
        }

        if (isRoot) {
            return new JSONObject().put(gson.toJson(this.getValue()), result);
        } else {
            return result;
        }
    }

    public static <T> SerialTreeItem<T> fromJson(String jsonString, Type type) {
        return fromJson(null, jsonString, type);
    }

    public static <T> SerialTreeItem<T> fromJson(SerialTreeItem<T> root, String jsonString, Type type) {
        JSONObject jsonObject = new JSONObject(jsonString);
        Gson gson = new GsonBuilder().create();

        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (root == null) {
                root = SerialTreeItem.fromJson(new SerialTreeItem<>(gson.fromJson(key, type)),
                        jsonObject.getJSONObject(key).toString(), type);
            } else {
                if (jsonObject.get(key) instanceof JSONObject) {
                    root.getChildren().add(SerialTreeItem.fromJson(new SerialTreeItem<>(gson.fromJson(key, type)),
                            jsonObject.getJSONObject(key).toString(), type));
                }
            }
        }

        return root;
    }

    public boolean insertByValue(SerialTreeItem<T> treeItem, Comparator<T> comparator) {
        return this.insertByValue(treeItem, this, comparator);
    }

    private boolean insertByValue(SerialTreeItem<T> treeItem, SerialTreeItem<T> tree, Comparator<T> comparator) {
        for (TreeItem<T> item : tree.getChildren()) {
            SerialTreeItem<T> serialTreeItem = (SerialTreeItem<T>) item;
            if (comparator.compare(treeItem.getValue(), item.getValue()) == 0) {
                item.getChildren().add(treeItem);
                return true;
            } else {
                if (serialTreeItem.insertByValue(treeItem, serialTreeItem, comparator)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void traverse(Action<T> action) {
        this.traverse(this, action);
    }

    private void traverse(SerialTreeItem<T> root, Action<T> action) {
        action.action(root.getValue());
        if (!root.isLeaf()) {
            for (TreeItem<T> item : root.getChildren()) {
                SerialTreeItem<T> serialTreeItem = (SerialTreeItem<T>) item;
                serialTreeItem.traverse(serialTreeItem, action);
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

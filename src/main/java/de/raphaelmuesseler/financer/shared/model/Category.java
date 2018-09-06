package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.client.ui.I18N;

import java.io.Serializable;

public class Category implements Serializable {
    private static final long serialVersionUID = -5848321222290793608L;
    public static final String[] CATEGORIES = {
            "fixedRevenue",
            "variableRevenue",
            "fixedExpenses",
            "variableExpenses"
    };

    private int id, parentId, rootId;
    private String name;
    private boolean isKey;

    public Category(String name) {
        this(-1, -1, -1, name, false);
    }

    public Category(int id, String name) {
        this(id, -1, -1, name, false);
    }

    public Category(int id, int parentId, String name) {
        this(id, parentId, -1, name, false);
    }

    public Category(String name, boolean isKey) {
        this(-1, -1, -1, name, isKey);
    }

    public Category(int id, String name, boolean isKey) {
        this(id, -1, -1, name, isKey);
    }

    public Category(int id, int parentId, int rootId, String name, boolean isKey) {
        this.id = id;
        this.parentId = parentId;
        this.rootId = rootId;
        this.name = name;
        this.isKey = isKey;
    }

    public String getName() {
        return isKey ? I18N.get(this.name) : name;
    }

    public String getKey() {
        return isKey ? name : null;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return (parentId == -1 ? rootId : parentId);
    }

    public int getRootId() {
        return rootId;
    }

    public boolean isKey() {
        return isKey;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setKey(boolean key) {
        isKey = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setRootId(int rootId) {
        this.rootId = rootId;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}


package de.raphaelmuesseler.financer.shared.model;

import java.io.Serializable;

public class Category implements Serializable {
    private static final long serialVersionUID = -5776418454648469541L;
    private int id, parentId, rootId;
    private String name, prefix = null;

    public Category(int id, String name, int parentId, int rootId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.rootId = rootId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getParentId() {
        return parentId;
    }

    public int getRootId() {
        return rootId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

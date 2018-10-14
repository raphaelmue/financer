package de.raphaelmuesseler.financer.shared.model;

public class Category {
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
}

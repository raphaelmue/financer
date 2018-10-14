package de.raphaelmuesseler.financer.shared.model;

public class Category {
    private final int id, parentId, rootId;
    private final String name;
    private String prefix = null;

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

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

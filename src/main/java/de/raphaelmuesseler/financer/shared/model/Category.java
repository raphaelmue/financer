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

    private int id, parentId;
    private final String name;
    private final boolean isKey;

    public Category(String name) {
        this(-1,  -1, name, false);
    }

    public Category(int id, String name) {
        this(id,  -1, name, false);
    }

    public Category(int id, int parentId, String name) {
        this(id,  parentId, name, false);
    }

    public Category(String name, boolean isKey) {
        this(-1, -1, name, isKey);
    }

    public Category(int id, int parentId, String name, boolean isKey) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.isKey = isKey;
    }

    public String getName() {
        if (isKey) {
            return I18N.get(this.name);
        } else {
            return name;
        }
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}


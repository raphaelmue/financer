package de.raphaelmuesseler.financer.shared.model.db;

import java.util.Comparator;

public class CategoryDAO implements Comparable<CategoryDAO>, DataAccessObject {
    private final static long serialVersionUID = 5491420625985358596L;

    private int id;
    private UserDAO user;
    private int categoryRoot;
    private int parentId;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDAO getUser() {
        return user;
    }

    public void setUser(UserDAO user) {
        this.user = user;
    }

    public int getCategoryRoot() {
        return categoryRoot;
    }

    public void setCategoryRoot(int categoryClass) {
        this.categoryRoot = categoryClass;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(CategoryDAO o) {
        return Comparator.comparing(CategoryDAO::getCategoryRoot)
                .thenComparing(CategoryDAO::getParentId)
                .thenComparing(CategoryDAO::getId)
                .compare(this, o);
    }
}

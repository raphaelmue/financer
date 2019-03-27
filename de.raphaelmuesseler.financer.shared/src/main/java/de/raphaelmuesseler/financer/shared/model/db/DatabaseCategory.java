package de.raphaelmuesseler.financer.shared.model.db;

public class DatabaseCategory {
    private int id;
    private DatabaseUser user;
    private int categoryClass;
    private DatabaseCategory parent;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DatabaseUser getUser() {
        return user;
    }

    public void setUser(DatabaseUser user) {
        this.user = user;
    }

    public int getCategoryClass() {
        return categoryClass;
    }

    public void setCategoryClass(int categoryClass) {
        this.categoryClass = categoryClass;
    }

    public DatabaseCategory getParent() {
        return parent;
    }

    public void setParent(DatabaseCategory parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

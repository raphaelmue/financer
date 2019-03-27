package de.raphaelmuesseler.financer.shared.model.db;

import java.util.Set;

public class DatabaseCategory {
    private int id;
    private DatabaseUser user;
    private int categoryClass;
    private DatabaseCategory parent;
    private String name;
    private Set<DatabaseTransaction> transactions;
    private Set<DatabaseFixedTransaction> fixedTransactions;

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

    public Set<DatabaseTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<DatabaseTransaction> transactions) {
        this.transactions = transactions;
    }

    public Set<DatabaseFixedTransaction> getFixedTransactions() {
        return fixedTransactions;
    }

    public void setFixedTransactions(Set<DatabaseFixedTransaction> fixedTransactions) {
        this.fixedTransactions = fixedTransactions;
    }
}

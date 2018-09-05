package de.raphaelmuesseler.financer.shared.model;

import de.raphaelmuesseler.financer.client.ui.I18N;

import java.io.Serializable;

public class Category implements Serializable {

    public static final String[] CATEGORIES = {
            "fixedRevenue",
            "variableRevenue",
            "fixedExpenses",
            "variableExpenses"
    };

    private static final long serialVersionUID = -5848321222290793608L;
    private final String name;
    private final boolean isKey;

    public Category(String name) {
        this(name, false);
    }

    public Category(String name, boolean isKey) {
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

    @Override
    public String toString() {
        return this.getName();
    }
}


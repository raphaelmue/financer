package de.raphaelmuesseler.financer.server.db;

public enum DatabaseName {
    DEV("financer_dev"),
    TEST("financer_test"),
    PROD("financer_prod");

    private final String name;

    DatabaseName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DatabaseName getByShortCut(String shortcut) {
        switch (shortcut) {
            case "dev":
                return DEV;
            case "test":
                return TEST;
            case "prod":
                return PROD;
        }
        return null;
    }
}

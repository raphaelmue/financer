package de.raphaelmuesseler.financer.server.db;

public class Database {

    private static final String HOST        = "raphael-muesseler.de";
    private static final String DB_NAME     = "financer_dev";
    // for production:
    // private static final String DB_NAME     = "financer_prod";
    private static final String DB_USER     = "financer_admin";
    private static final String DB_PASSWORD = "";

    private static Database INSTANCE = null;

    Database() {
        // initializing DB access
    }

    /**
     * Returns the current instance of the database
     * @return database object
     */
    public static Database getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }
}

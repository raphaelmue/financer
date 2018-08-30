package de.raphaelmuesseler.financer.server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.raphaelmuesseler.financer.server.util.Converter;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String HOST = "raphael-muesseler.de";
    private static final String DB_NAME = "financer_dev";
    // for production:
    // private static final String DB_NAME     = "financer_prod";
    private static final String DB_USER = "financer_admin";
    private static final String DB_PASSWORD = "XTS0NvvNlZ3roWqY";

    private Connection connection;

    private static Database INSTANCE = null;

    private Database() throws SQLException {
        try {
            // loading database driver
            Class.forName(JDBC_DRIVER);

            // initializing DB access
            this.connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/" + DB_NAME +
                            "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&" +
                            "serverTimezone=UTC",
                    DB_USER, DB_PASSWORD);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the current instance of the database
     *
     * @return database object
     */
    public static Database getInstance() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new Database();
        }
        return INSTANCE;
    }

    public Object get(String tableName, Type resultType) throws SQLException {
        return this.get(tableName, resultType, new HashMap<>());
    }

    public Object get(String tableName, Type resultType, Map<String, Object> where) throws SQLException {
        Statement statement = null;
        ResultSet result = null;
        Gson gson = new GsonBuilder().create();
        statement = connection.createStatement();
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName);

        // setting where-clause
        if (where.size() > 0) {
            query.append(" WHERE ");
            boolean first = true;
            for (Map.Entry<String, Object> entry : where.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    query.append(" AND ");
                }
                query.append(entry.getKey()).append(" = '").append(entry.getValue()).append("'");
            }
        }

        // execute query
        result = statement.executeQuery(query.toString());

        String json = Converter.convertResultSetIntoJSON(result)
                .toString()
                .replace("[", "")
                .replace("]", "");

        try {
            return gson.fromJson(json, resultType);
        } catch (JsonParseException e) {
            return null;
        }
    }
}

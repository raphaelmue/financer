package de.raphaelmuesseler.financer.server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.raphaelmuesseler.financer.server.util.Converter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Database {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    // for testing:
    private static final String HOST_LOCAL = "localhost";

    // for deployment:
    private static final String HOST_DEPLOY = "raphael-muesseler.de";

    private static String HOST;
    private static final String DB_NAME = "financer_dev";
    // for production:
    // private static final String DB_NAME     = "financer_prod";
    private static final String DB_USER = "financer_admin";
    private static final String DB_PASSWORD = "XTS0NvvNlZ3roWqY";

    private Connection connection;

    private static Database INSTANCE = null;

    public enum Table {
        FIXED_TRANSACTIONS("fixed_transactions"),
        FIXED_TRANSACTIONS_AMOUNTS("fixed_transactions_amounts"),
        TRANSACTIONS("transactions"),
        USERS("users"),
        USERS_CATEGORIES("users_categories");

        private String tableName;

        Table(String tableName) {
            this.tableName = tableName;
        }

        public String getTableName() {
            return tableName;
        }

        @Override
        public String toString() {
            return this.getTableName();
        }
    }

    private Database() throws SQLException {
        try {
            // loading database driver
            Class.forName(JDBC_DRIVER);

            // initializing DB access
            this.connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":3306/" + DB_NAME +
                            "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&" +
                            "serverTimezone=UTC&autoReconnect=true",
                    DB_USER, DB_PASSWORD);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the static host. This method should be called before the first call of getInstance.
     *
     * @param local sets the host to 'localhost' if true
     */
    public static void setHost(boolean local) {
        Database.HOST = local ? Database.HOST_LOCAL : Database.HOST_DEPLOY;
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

    /**
     * Fetches data from the database and parses via the Gson library it to an object.
     *
     * @param table           Database table to fetch from
     * @param resultType      Type of object, which will be returned
     * @param whereParameters Where parameters which will be concatenated by AND
     * @return Object with fetched data
     * @throws SQLException thrown, when something went wrong executing the SQL statement
     */
    public List<Object> getObject(Table table, Type resultType, Map<String, Object> whereParameters) throws SQLException {
        Gson gson = new GsonBuilder().create();
        JSONArray jsonArray = this.get(table, whereParameters);
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            try {
                result.add(gson.fromJson(jsonObject.toString(), resultType));
            } catch (JsonParseException ignored) {
            }
        }
        return result;
    }

    /**
     * {@code orderByClause} set to null by default.
     *
     * @see Database#get(Table, Map, String)
     */
    public JSONArray get(Table table, Map<String, Object> whereParameters) throws SQLException {
        return this.get(table, whereParameters, null);
    }

    /**
     * @param tableName       database table to fetch from
     * @param whereParameters where parameters which will be concatenated by AND
     * @param orderByClause   order by clause which will be appended on the sql statement
     * @return JSONArray that contains
     * @throws SQLException thrown, when something went wrong executing the SQL statement
     */
    // TODO select specific fields
    // TODO escape strings
    public JSONArray get(Table tableName, Map<String, Object> whereParameters, String orderByClause) throws SQLException {
        PreparedStatement statement;
        String query = "SELECT * FROM " + tableName.getTableName() +
                this.getClause(whereParameters, "WHERE", " AND ") +
                this.getOrderByClause(orderByClause);

        statement = connection.prepareStatement(query);
        this.preparedStatement(statement, whereParameters);

        // execute query
        ResultSet result = statement.executeQuery();
        return Converter.convertResultSetIntoJSON(result);
    }

    /**
     * Inserts one data row into database.
     *
     * @param tableName table to insert
     * @param values    this map contains the all the corresponding column values that will be inserted
     * @throws SQLException thrown, when something went wrong executing the SQL statement
     */
    public void insert(Table tableName, Map<String, Object> values) throws SQLException {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " ")
                .append(values.keySet().toString().replace("[", "(").replace("]", ")"))
                .append(" values (");

        boolean first = true;
        for (int i = 0; i < values.size(); i++) {
            if (first) {
                first = false;
            } else {
                query.append(", ");
            }
            query.append("?");
        }
        query.append(")");
        statement = this.preparedStatement(connection.prepareStatement(query.toString()), values);

        statement.execute();
    }

    /**
     * Updates one or multiple data rows that matches the where condition.
     *
     * @param tableName       database table to be updated
     * @param whereParameters where condition to update only specific data rows
     * @param values          values that will be updated
     * @throws SQLException thrown, when something went wrong executing the SQL statement
     */
    public void update(Table tableName, Map<String, Object> whereParameters, Map<String, Object> values) throws SQLException {
        PreparedStatement statement;
        String query = "UPDATE " + tableName.getTableName() +
                this.getClause(values, "SET", ", ") +
                this.getClause(whereParameters, "WHERE", " AND ");

        statement = connection.prepareStatement(query);
        this.preparedStatement(statement, values);
        this.preparedStatement(statement, whereParameters, values.size() + 1);
        statement.execute();

    }

    /**
     * Deletes one or multiple data rows that matches the where condition.
     *
     * @param table           database table to obe updated
     * @param whereParameters where condition to delete specific data rows
     * @throws SQLException thrown, when something went wrong executing the SQL statement
     */
    public void delete(Table table, Map<String, Object> whereParameters) throws SQLException {
        PreparedStatement statement;
        String query = "DELETE FROM " + table.getTableName() + " " +
                this.getClause(whereParameters, "WHERE", " AND ");
        statement = this.preparedStatement(connection.prepareStatement(query), whereParameters);
        statement.execute();
    }

    private String getClause(Map<String, Object> values, String operation, String separator) {
        StringBuilder whereClauseString = new StringBuilder();
        if (values.size() > 0) {
            whereClauseString.append(" ").append(operation).append(" ");
            boolean first = true;
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    whereClauseString.append(separator);
                }
                whereClauseString.append(entry.getKey()).append(" = ?");
            }
        }
        return whereClauseString.toString();
    }

    private PreparedStatement preparedStatement(PreparedStatement statement, Map<String, Object> values) throws SQLException {
        return this.preparedStatement(statement, values, 1);
    }

    private PreparedStatement preparedStatement(PreparedStatement statement, Map<String, Object> values, int index) throws SQLException {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            statement.setObject(index, entry.getValue());
            index++;
        }
        return statement;
    }

    private String getOrderByClause(String field) {
        StringBuilder orderByClauseString = new StringBuilder();
        if (field != null) {
            orderByClauseString.append(" ORDER BY ").append(field);
        }
        return orderByClauseString.toString();
    }
}

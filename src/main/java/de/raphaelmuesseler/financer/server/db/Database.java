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
import java.util.HashMap;
import java.util.List;
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

    public enum Table {
        CATEGORIES("categories"),
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

    public List<Object> getObject(Table tableName, Type resultType) throws SQLException {
        return this.getObject(tableName, resultType, new HashMap<>());
    }

    public List<Object> getObject(Table tableName, Type resultType, Map<String, Object> whereParameters) throws SQLException {
        Gson gson = new GsonBuilder().create();
        JSONArray jsonArray = this.get(tableName, whereParameters);
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

    public JSONArray get(Table tableName, Map<String, Object> whereParameters) throws SQLException {
        return this.get(tableName, whereParameters, null);
    }

    // TODO select specific fields
    // TODO escape strings
    public JSONArray get(Table tableName, Map<String, Object> whereParameters, String orderByClause) throws SQLException {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName.getTableName())
                .append(this.getClause(whereParameters, "WHERE", " AND "))
                .append(this.getOrderByClause(orderByClause));

        // execute query
        statement = connection.prepareStatement(query.toString());
        this.preparedStatement(statement, whereParameters);
        ResultSet result = statement.executeQuery();
        return Converter.convertResultSetIntoJSON(result);
    }

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
        statement = connection.prepareStatement(query.toString());
        statement = this.preparedStatement(statement, values);

        statement.execute();
    }

    public void update(Table tableName, Map<String, Object> whereParameters, Map<String, Object> values) throws SQLException {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder("UPDATE " + tableName.getTableName())
                .append(this.getClause(values, "SET", ", "))
                .append(this.getClause(whereParameters, "WHERE", " AND "));

        statement = connection.prepareStatement(query.toString());
        this.preparedStatement(statement, values);
        this.preparedStatement(statement, whereParameters, values.size() + 1);
        statement.execute();

    }

    public void delete(Table table, Map<String, Object> whereParameters) throws SQLException {
        PreparedStatement statement;
        StringBuilder query = new StringBuilder("DELETE FROM " + table.getTableName() + " ")
                .append(this.getClause(whereParameters, "WHERE", " AND "));

        statement = connection.prepareStatement(query.toString());
        statement = this.preparedStatement(statement, whereParameters);

        statement.execute();
    }

    private String getClause(Map<String, Object> values, String operation, String seperator) {
        StringBuilder whereClauseString = new StringBuilder();
        if (values.size() > 0) {
            whereClauseString.append(" " + operation + " ");
            boolean first = true;
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    whereClauseString.append(seperator);
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

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

    public List<Object> getObject(String tableName, Type resultType) throws SQLException {
        return this.getObject(tableName, resultType, new HashMap<>());
    }

    public List<Object> getObject(String tableName, Type resultType, Map<String, Object> whereParameters) throws SQLException {
        Gson gson = new GsonBuilder().create();
        JSONArray jsonArray = this.get(tableName, whereParameters);
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            try {
                 result.add(gson.fromJson(jsonObject.toString(), resultType));
            } catch (JsonParseException ignored) { }
        }
        return result;
    }
    // TODO select specific fields
    public JSONArray get(String tableName, Map<String, Object> whereParameters) throws SQLException {
        Statement statement = connection.createStatement();
        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName);
        query.append(this.getWhereClause(whereParameters));

        // execute query
        ResultSet result = statement.executeQuery(query.toString());

        return Converter.convertResultSetIntoJSON(result);
    }

    private String getWhereClause(Map<String, Object> whereParameters) {
        StringBuilder whereClauseString = new StringBuilder();
        if (whereParameters.size() > 0) {
            whereClauseString.append(" WHERE ");
            boolean first = true;
            for (Map.Entry<String, Object> entry : whereParameters.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    whereClauseString.append(" AND ");
                }
                whereClauseString.append(entry.getKey()).append(" = '").append(entry.getValue()).append("'");
            }
        }
        return whereClauseString.toString();
    }
}

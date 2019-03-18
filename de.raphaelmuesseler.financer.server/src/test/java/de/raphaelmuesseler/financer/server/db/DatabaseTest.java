package de.raphaelmuesseler.financer.server.db;

import de.raphaelmuesseler.financer.util.Hash;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseTest {
    private static Database database;

    @BeforeAll
    public static void createDatabaseConnection() throws SQLException {
        Database.setHost(false);
        Database.setDbName(Database.DatabaseName.TEST);
        database = Database.getInstance();
    }

    @BeforeEach
    public void cleanDB() throws SQLException {
        database.clearDatabase();

        String salt = "VQoX3kxwjX3gOOY1Jixk)Dc$0y$e4B!9";
        String hashedPassword = Hash.create("password", salt);

        Map<String, Object> testValues = new HashMap<>();
        testValues.put("email", "nobody@test.com");
        testValues.put("name", "Nobody");
        testValues.put("surname", "Test");
        testValues.put("password", hashedPassword);
        testValues.put("salt", salt);
        testValues.put("birthdate", "2000-02-20");
        testValues.put("gender", "male");
        database.insert(Database.Table.USERS, testValues);

        testValues.clear();
        testValues.put("email", "second@test.com");
        testValues.put("name", "Second");
        testValues.put("surname", "Test");
        testValues.put("password", hashedPassword);
        testValues.put("salt", salt);
        testValues.put("birthdate", "2000-02-20");
        testValues.put("gender", "male");
        database.insert(Database.Table.USERS, testValues);
    }

    @Test
    public void testReadLatestID() throws SQLException {
        Assertions.assertEquals(2, database.getLatestId(Database.Table.USERS));
    }

    @Test
    public void testDelete() throws SQLException {
        HashMap<String, Object> whereParams = new HashMap<>();
        whereParams.put("id", "1");
        database.delete(Database.Table.USERS, whereParams);

        Assertions.assertEquals(0, database.get(Database.Table.USERS, whereParams).length());
    }

    @Test
    public void testGet() throws SQLException {
        HashMap<String, Object> whereParams = new HashMap<>();
        whereParams.put("id", "1");
        JSONArray result = database.get(Database.Table.USERS, whereParams);

        Assertions.assertEquals(1, result.length());
        Assertions.assertEquals(1, result.getJSONObject(0).getInt("id"));
    }

    @Test
    public void testUpdate() throws SQLException {
        HashMap<String, Object> whereParams = new HashMap<>();
        whereParams.put("id", "1");
        HashMap<String, Object> toUpdate = new HashMap<>();
        toUpdate.put("name", "test");
        toUpdate.put("surname", "nobody");
        database.update(Database.Table.USERS, whereParams, toUpdate);
        JSONArray result = database.get(Database.Table.USERS, whereParams);

        Assertions.assertEquals("test", result.getJSONObject(0).getString("name"));
        Assertions.assertEquals("nobody", result.getJSONObject(0).getString("surname"));
    }

    @Test
    public void testGetWithOrderBy() throws SQLException {
        HashMap<String, Object> whereParams = new HashMap<>();
        JSONArray result = database.get(Database.Table.USERS, whereParams, "id DESC");
        Assertions.assertEquals(2, result.getJSONObject(0).getInt("id"));
    }
}

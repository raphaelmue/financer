package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.util.Hash;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinancerService {

    private static FinancerService INSTANCE = null;
    private Database database;

    private FinancerService() {
        try {
            this.database = Database.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static FinancerService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FinancerService();
        }
        return INSTANCE;
    }

    /**
     * Checks, if the users credentials are correct
     *
     * @param parameters [email, password]
     * @return true, if credentials are correct
     */
    public ConnectionResult<User> checkCredentials(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Checking credetials ...");

        Map<String, Object> whereEmail = new HashMap<>();
        whereEmail.put("email", parameters.get("email"));

        User user = (User) this.database.getObject("users", User.class, whereEmail).get(0);
        if (user != null) {
            String password = Hash.create((String) parameters.get("password"), user.getSalt());
            if (password.equals(user.getPassword())) {
                logger.log(Level.INFO, "Credentials of user '" + user.getFullName() + "' are approved.");
            } else {
                user = null;
            }
        }

        if (user == null) {
            logger.log(Level.INFO, "Credentials are incorrect.");
        }

        return new ConnectionResult<>(user);
    }

    public ConnectionResult<String> getUsersCategories(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Fetching users categories ...");

        Map<String, Object> whereClause = new HashMap<>();
        whereClause.put("id", ((User) parameters.get("user")).getId());

        JSONObject result = new JSONObject();

        for (int i = 0; i < 4; i++) {
            whereClause.put("cat_id", i);
            JSONArray jsonArray = this.database.get("users_categories", whereClause);
            if (jsonArray.length() > 0) {
                result.put(Integer.toString(i), new JSONObject(jsonArray.getJSONObject(0).get("structure").toString()));
            }
        }

        return new ConnectionResult<>(result.toString());
    }
}

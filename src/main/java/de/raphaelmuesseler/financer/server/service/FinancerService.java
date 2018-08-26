package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.util.Hash;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.User;

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
    public ConnectionResult<Boolean> checkCredentials(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Checking credetials ...");
        boolean result = false;

        Map<String, Object> whereEmail = new HashMap<>();
        whereEmail.put("email", parameters.get("email"));

        User user = null;
        try {
            user = (User) this.database.get("users", User.class, whereEmail);
            if (user != null) {
                String password = Hash.create((String) parameters.get("password"), user.getSalt());
                if (password.equals(user.getPassword())) {
                    result = true;
                    logger.log(Level.INFO,"Credentials of user '" + user.getFullName() + "' are approved.");
                }
            }
        } catch (Exception ignored) { }

        if (!result) {
            logger.log(Level.INFO,"Credentials of user '" + user.getFullName() + "' incorrect.");
        }

        return new ConnectionResult<>(result);
    }

}

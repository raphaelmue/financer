package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.util.Hash;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.util.SerialTreeItem;
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
        whereClause.put("user_id", ((User) parameters.get("user")).getId());

        SerialTreeItem<Category> tree = new SerialTreeItem<>(new Category("categories", true));

        for (int i = 0; i < 4; i++) {
            whereClause.put("cat_id", i);
            JSONArray jsonArray = this.database.get("users_categories", whereClause, "cat_id ASC, parent_id ASC");

            SerialTreeItem<Category> subTree = new SerialTreeItem<>(new Category(Category.CATEGORIES[i], true));

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);

                if (jsonObject.get("parent_id").equals("null")) {
                    subTree.getChildren().add(new SerialTreeItem<>(new Category(jsonObject.getInt("id"), jsonObject.getString("name"))));
                } else {
                    subTree.insertByValue(new SerialTreeItem<>(new Category(jsonObject.getInt("id"), jsonObject.getInt("parent_id"),
                            jsonObject.getString("name"))), (o1, o2) -> Integer.compare(o1.getParentId(), o2.getId()));
                }
            }

            tree.getChildren().add(subTree);

        }
        return new ConnectionResult<>(tree.getJson().toString());
    }
}

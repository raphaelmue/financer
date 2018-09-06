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

        User user = (User) this.database.getObject(Database.Table.USERS, User.class, whereEmail).get(0);
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
            JSONArray jsonArray = this.database.get(Database.Table.USERS_CATEGORIES, whereClause,
                    "cat_id ASC, parent_id ASC");

            SerialTreeItem<Category> subTree = new SerialTreeItem<>(new Category(i, -1, i, Category.CATEGORIES[i], true));

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);

                if (jsonObject.get("parent_id").equals("null")) {
                    subTree.getChildren().add(new SerialTreeItem<>(new Category(jsonObject.getInt("id"), -1, i,
                            jsonObject.getString("name"), false)));
                } else {
                    System.out.println(i);
                    subTree.insertByValue(new SerialTreeItem<>(new Category(jsonObject.getInt("id"),
                                    (jsonObject.getInt("parent_id")), i,
                                    jsonObject.getString("name"), false)),
                            (o1, o2) -> Integer.compare(o1.getParentId(), o2.getId()));
                }
            }

            tree.getChildren().add(subTree);

        }
        return new ConnectionResult<>(tree.getJson().toString());
    }

    public ConnectionResult<Boolean> updateUsersCategories(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Updating users categories ...");
        boolean result = false;
        User user = (User) parameters.get("user");
        SerialTreeItem<Category> tree = SerialTreeItem.fromJson((String) parameters.get("tree"), Category.class);

        tree.traverse(category -> {
            if (!category.isKey()) {
                System.out.println(category.getName() + " " + category.getId() + " " + category.getParentId() + " " + category.getRootId());

                Map<String, Object> whereClause = new HashMap<>();
                whereClause.put("id", category.getId());
//                try {
//                    Map<String, Object> values = new HashMap<>();
//                    values.put("name", category.getName());
//
//                    JSONArray jsonArray = this.database.get(Database.Table.USERS_CATEGORIES, whereClause);
//                    if (jsonArray.length() > 0) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(0);
//                        this.database.update(Database.Table.USERS_CATEGORIES, whereClause, values);
//                    } else {
//                        values.put("parent_id", (category.getParentId() == -1 ? null : category.getParentId()));
//                        values.put("cat_id", category.getRootId());
//                        values.put("user_id", user.getId());
//                        this.database.insert(Database.Table.USERS_CATEGORIES, values);
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
            }
        });

        return new ConnectionResult<>(result);
    }
}

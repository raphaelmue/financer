package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.util.Hash;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.util.collections.SerialTreeItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public ConnectionResult<Category> addCategory(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Adding new category ...");
        User user = (User) parameters.get("user");
        Category category = (Category) parameters.get("category");

        Map<String, Object> values = new HashMap<>();
        values.put("user_id", user.getId());
        values.put("cat_id", category.getRootId());
        if (category.getParentId() != -1 ) {
            values.put("parent_id", category.getParentId());
        }
        values.put("name", category.getName());

        this.database.insert(Database.Table.USERS_CATEGORIES, values);

        return new ConnectionResult<>((Category) this.database.getObject(Database.Table.USERS_CATEGORIES, Category.class, values).get(0));
    }

    public ConnectionResult<Void> updateCategory(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Updating users categories ...");
        Category category = (Category) parameters.get("category");

        Map<String, Object> where = new HashMap<>();
        where.put("id", category.getId());

        Map<String, Object> values = new HashMap<>();
        values.put("name", category.getName());

        this.database.update(Database.Table.USERS_CATEGORIES, where, values);

        return new ConnectionResult<>(null);
    }

    public ConnectionResult<Void> deleteCategory(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Deleting category ...");
        Category category = (Category) parameters.get("category");

        Map<String, Object> where = new HashMap<>();
        where.put("id", category.getId());

        this.database.delete(Database.Table.USERS_CATEGORIES, where);

        return new ConnectionResult<>(null);
    }

    public ConnectionResult<List<Transaction>> getTransactions(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Fetching users transaction ...");
        List<Transaction> transactions = new ArrayList<>();

        Map<String, Object> whereClause = new HashMap<>();
        whereClause.put("user_id", ((User) parameters.get("user")).getId());

        JSONArray jsonArray = this.database.get(Database.Table.TRANSACTIONS, whereClause);
        whereClause.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObjectTransaction = jsonArray.getJSONObject(i);

            whereClause.put("id", jsonObjectTransaction.getInt("cat_id"));
            JSONObject jsonObjectCategory = this.database.get(Database.Table.USERS_CATEGORIES, whereClause).getJSONObject(0);
            Category category = new Category(jsonObjectCategory.getInt("id"), jsonObjectCategory.getInt("parent_id"),
                    jsonObjectCategory.getInt("cat_id"), jsonObjectCategory.getString("name"), false);

            transactions.add(new Transaction(jsonObjectTransaction.getInt("id"), jsonObjectTransaction.getDouble("amount"), category,
                    jsonObjectTransaction.getString("product"), jsonObjectTransaction.getString("purpose"),
                    ((Date) jsonObjectTransaction.get("value_date")).toLocalDate(),
                    jsonObjectTransaction.getString("shop")));
        }
        return new ConnectionResult<>(transactions);
    }

    public ConnectionResult<Void> addTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Adding transaction ...");
        User user = (User) parameters.get("user");
        Transaction transaction = (Transaction) parameters.get("transaction");

        Map<String, Object> values = new HashMap<>();
        values.put("user_id", user.getId());
        values.put("value_date", transaction.getValueDate());
        values.put("amount", transaction.getAmount());
        values.put("product", transaction.getProduct());
        values.put("purpose", transaction.getPurpose());
        values.put("shop", transaction.getShop());
        values.put("cat_id", transaction.getCategory().getId());

        this.database.insert(Database.Table.TRANSACTIONS, values);

        return new ConnectionResult<>(null);
    }


    public ConnectionResult<Void> updateTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Adding transaction ...");
        User user = (User) parameters.get("user");
        Transaction transaction = (Transaction) parameters.get("transaction");

        Map<String, Object> where = new HashMap<>();
        where.put("id", transaction.getId());

        Map<String, Object> values = new HashMap<>();
        values.put("value_date", transaction.getValueDate());
        values.put("amount", transaction.getAmount());
        values.put("product", transaction.getProduct());
        values.put("purpose", transaction.getPurpose());
        values.put("shop", transaction.getShop());
        values.put("cat_id", transaction.getCategory().getId());

        this.database.update(Database.Table.TRANSACTIONS, where, values);

        return new ConnectionResult<>(null);
    }

    public ConnectionResult<Void> deleteTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Adding transaction ...");
        Transaction transaction = (Transaction) parameters.get("transaction");

        Map<String, Object> where = new HashMap<>();
        where.put("id", transaction.getId());

        this.database.delete(Database.Table.TRANSACTIONS, where);

        return new ConnectionResult<>(null);
    }
}

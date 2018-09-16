package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.util.Hash;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import de.raphaelmuesseler.financer.shared.util.collections.SerialTreeItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
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
        if (category.getParentId() != -1) {
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

    public ConnectionResult<List<FixedTransaction>> getFixedTransactions(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Fetching fixed transactions ...");

        Map<String, Object> whereClause = new HashMap<>();
        whereClause.put("user_id", ((User) parameters.get("user")).getId());

        List<FixedTransaction> fixedTransactions = new ArrayList<>();

        JSONArray jsonArray = this.database.get(Database.Table.FIXED_TRANSACTIONS, whereClause, "start_date DESC");
        for (int i = 0; i < jsonArray.length(); i++) {
            whereClause.clear();

            JSONObject jsonObjectTransaction = jsonArray.getJSONObject(i);

            // fetching category object
            whereClause.put("id", jsonObjectTransaction.getInt("cat_id"));
            JSONObject jsonObjectCategory = this.database.get(Database.Table.USERS_CATEGORIES, whereClause).getJSONObject(0);
            whereClause.clear();
            Category category = new Category(jsonObjectCategory.getInt("id"),
                    (jsonObjectCategory.get("parent_id") == "null" ? -1 : jsonObjectCategory.getInt("parent_id")),
                    jsonObjectCategory.getInt("cat_id"),
                    jsonObjectCategory.getString("name"), false);

            // fetching respective transaction amounts if the flag "is_variable" is true
            List<TransactionAmount> transactionAmounts = new ArrayList<>();
            if (jsonObjectTransaction.getInt("is_variable") == 1) {
                whereClause.clear();
                whereClause.put("fixed_transaction_id", jsonObjectTransaction.getInt("id"));
                JSONArray jsonArrayTransactionAmount = this.database.get(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, whereClause);
                for (int j = 0; j < jsonArrayTransactionAmount.length(); j++) {
                    JSONObject jsonObjectTransactionAmount = jsonArrayTransactionAmount.getJSONObject(j);
                    transactionAmounts.add(new TransactionAmount(jsonObjectTransactionAmount.getInt("id"),
                            jsonObjectTransactionAmount.getDouble("amount"),
                            ((Date) jsonObjectTransactionAmount.get("value_date")).toLocalDate()));
                }
            }

            fixedTransactions.add(new FixedTransaction(jsonObjectTransaction.getInt("id"),
                    (jsonObjectTransaction.get("amount") == "null" ? 0 : jsonObjectTransaction.getDouble("amount")),
                    category,
                    (jsonObjectTransaction.get("product") == "null" ? null : jsonObjectTransaction.getString("product")),
                    (jsonObjectTransaction.get("purpose") == "null" ? null : jsonObjectTransaction.getString("purpose")),
                    ((Date) jsonObjectTransaction.get("start_date")).toLocalDate(),
                    (jsonObjectTransaction.get("end_date") == "null" ? null : ((Date) jsonObjectTransaction.get("end_date")).toLocalDate()),
                    (jsonObjectTransaction.getInt("is_variable") == 1),
                    jsonObjectTransaction.getInt("day"),
                    transactionAmounts));
        }

        return new ConnectionResult<>(fixedTransactions);
    }

    public ConnectionResult<FixedTransaction> addFixedTransactions(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Adding fixed transactions ...");
        User user = (User) parameters.get("user");
        FixedTransaction fixedTransaction = (FixedTransaction) parameters.get("fixedTransaction");

        Map<String, Object> whereParameters = new HashMap<>();
        whereParameters.put("user_id", user.getId());
        whereParameters.put("cat_id", fixedTransaction.getCategory().getId());
        whereParameters.put("end_date", null);

        Map<String, Object> values = new HashMap<>();
        values.put("end_date", LocalDate.now().toString());

        this.database.update(Database.Table.FIXED_TRANSACTIONS, whereParameters, values);

        values.clear();
        values.put("user_id", user.getId());
        values.put("amount", (fixedTransaction.isVariable() ? null : fixedTransaction.getAmount()));
        values.put("cat_id", fixedTransaction.getCategory().getId());
        values.put("start_date", fixedTransaction.getStartDate());
        values.put("end_date", fixedTransaction.getEndDate());
        values.put("is_variable", (fixedTransaction.isVariable() ? 1 : 0));
        values.put("day", fixedTransaction.getDay());

        this.database.insert(Database.Table.FIXED_TRANSACTIONS, values);

        return new ConnectionResult<>(null);
    }


    public ConnectionResult<Void> updateFixedTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Updating fixed transaction ...");
        FixedTransaction fixedTransaction = (FixedTransaction) parameters.get("fixedTransaction");

        Map<String, Object> whereParameters = new HashMap<>();
        whereParameters.put("id", fixedTransaction.getId());

        Map<String, Object> values = new HashMap<>();
        values.put("amount", (fixedTransaction.isVariable() ? null : fixedTransaction.getAmount()));
        values.put("start_date", fixedTransaction.getStartDate());
        values.put("end_date", fixedTransaction.getEndDate());
        values.put("is_variable", (fixedTransaction.isVariable() ? 1 : 0));
        values.put("day", fixedTransaction.getDay());

        this.database.update(Database.Table.FIXED_TRANSACTIONS, whereParameters, values);
        whereParameters.clear();
        values.clear();

        for (TransactionAmount transactionAmount : fixedTransaction.getTransactionAmounts()) {
            whereParameters.put("id", transactionAmount.getId());

            values.put("fixed_transaction_id", fixedTransaction.getId());
            values.put("value_date", transactionAmount.getValueDate());
            values.put("amount", transactionAmount.getAmount());
            if (transactionAmount.getId() < 0) {
                this.database.insert(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, values);
            } else {
                this.database.update(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, whereParameters, values);
            }
        }

        return new ConnectionResult<>(null);
    }

    public ConnectionResult<Void> deleteFixedTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Deleting fixed transaction ...");
        Map<String, Object> whereParameters = new HashMap<>();
        whereParameters.put("id", ((FixedTransaction) parameters.get("fixedTransaction")).getId());
        this.database.delete(Database.Table.FIXED_TRANSACTIONS, whereParameters);

        whereParameters.clear();
        whereParameters.put("fixed_transaction_id", ((FixedTransaction) parameters.get("fixedTransaction")).getId());
        this.database.delete(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, whereParameters);
        return new ConnectionResult<>(null);
    }
}

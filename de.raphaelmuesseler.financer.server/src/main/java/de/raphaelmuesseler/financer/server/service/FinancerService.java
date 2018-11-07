package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.exceptions.EmailAlreadyInUseException;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseObject;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinancerService {

    private static FinancerService INSTANCE = null;
    private Database database;
    private RandomString tokenGenerator = new RandomString(64);

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
     * Checks, whether the token is valid and not expired and returns the corresponding user.
     *
     * @param token token string
     * @return User that has this token
     * @throws SQLException thrown, when something went wrong executing the SQL statement
     */
    public User checkUsersToken(Logger logger, String token) throws SQLException {
        logger.log(Level.INFO, "Checking users token ...");

        Map<String, Object> whereParameters = new HashMap<>();
        whereParameters.put("token", token);

        JSONArray jsonArray = this.database.get(Database.Table.USERS_TOKENS, whereParameters);

        if (jsonArray.length() == 1 && ((Date) jsonArray.getJSONObject(0).get("expire_date")).toLocalDate().compareTo(LocalDate.now()) >= 0) {
            whereParameters.clear();
            whereParameters.put("id", jsonArray.getJSONObject(0).get("user_id"));

            User user = (User) new User().fromDatabaseObject(this.database.getObject(Database.Table.USERS, DatabaseUser.class, whereParameters).get(0));
            if (user != null) {
                logger.log(Level.INFO, "Token of user '" + user.getFullName() + "' is approved");

                // update expire date
                whereParameters.clear();
                whereParameters.put("token", token);

                Map<String, Object> values = new HashMap<>();
                values.put("expire_date", LocalDate.now().plusMonths(1));

                this.database.update(Database.Table.USERS_TOKENS, whereParameters, values);

                return user;
            }
        }
        logger.log(Level.INFO, "Token is invalid");

        return null;
    }

    /**
     * Checks, if the users credentials are correct
     *
     * @param parameters [email, password]
     * @return true, if credentials are correct
     */
    public ConnectionResult<User> checkCredentials(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Checking credentials ...");

        Map<String, Object> whereEmail = new HashMap<>();
        whereEmail.put("email", parameters.get("email"));

        User user = null;

        List<DatabaseObject> result = this.database.getObject(Database.Table.USERS, DatabaseUser.class, whereEmail);
        if (result != null && result.size() == 1) {
            user = (User) new User().fromDatabaseObject(result.get(0));
            if (user != null) {
                String password = Hash.create((String) parameters.get("password"), user.getSalt());
                if (password.equals(user.getPassword())) {
                    logger.log(Level.INFO, "Credentials of user '" + user.getFullName() + "' are approved.");

                    String token = this.tokenGenerator.nextString();
                    Map<String, Object> values = new HashMap<>();
                    values.put("user_id", user.getId());
                    values.put("token", token);
                    values.put("expire_date", LocalDate.now().plusMonths(1));
                    this.database.insert(Database.Table.USERS_TOKENS, values);

                    user.setToken(token);
                } else {
                    user = null;
                }
            }
        }

        if (user == null) {
            logger.log(Level.INFO, "Credentials are incorrect.");
        }

        return new ConnectionResult<>(user);
    }

    public ConnectionResult<User> registerUser(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Registering new user ...");
        User user = (User) parameters.get("user");

        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());

        List<? extends DatabaseObject> result = this.database.getObject(Database.Table.USERS, User.class, values);
        if (result.size() > 0) {
            throw new EmailAlreadyInUseException((User) result.get(0));
        }

        values.put("password", user.getPassword());
        values.put("salt", user.getSalt());
        values.put("name", user.getName());
        values.put("surname", user.getSurname());
        values.put("birthdate", user.getBirthDate());

        this.database.insert(Database.Table.USERS, values);

        user.setId(this.database.getLatestId(Database.Table.USERS));

        // creating new token and inserting it to database
        String token = this.tokenGenerator.nextString();
        values.clear();
        values.put("user_id", user.getId());
        values.put("token", token);
        values.put("expire_date", LocalDate.now().plusMonths(1));
        this.database.insert(Database.Table.USERS_TOKENS, values);

        user.setToken(token);

        return new ConnectionResult<>(user);
    }

    public ConnectionResult<BaseCategory> getUsersCategories(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Fetching users categories ...");

        Map<String, Object> whereClause = new HashMap<>();
        whereClause.put("user_id", ((User) parameters.get("user")).getId());

        BaseCategory baseCategory = new BaseCategory();

        for (BaseCategory.CategoryClass categoryClass : BaseCategory.CategoryClass.values()) {
            whereClause.put("cat_id", categoryClass.getIndex());
            JSONArray jsonArray = this.database.get(Database.Table.USERS_CATEGORIES, whereClause,
                    "cat_id ASC, parent_id ASC");

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);

                if (jsonObject.get("parent_id").equals("null")) {
                    ((List<Tree<Category>>) baseCategory.getCategoryTreeByCategoryClass(categoryClass).getChildren()).add(
                            new CategoryTree(categoryClass, baseCategory.getCategoryTreeByCategoryClass(categoryClass),
                                    new Category(jsonObject.getInt("id"),
                                            jsonObject.getString("name"),
                                            (jsonObject.get("parent_id") == "null" ? -1 : jsonObject.getInt("parent_id")),
                                            jsonObject.getInt("cat_id"))));
                } else {
                    TreeUtil.insertByValue(baseCategory.getCategoryTreeByCategoryClass(categoryClass), new CategoryTree(categoryClass, null,
                                    new Category(jsonObject.getInt("id"),
                                            jsonObject.getString("name"),
                                            jsonObject.getInt("parent_id"),
                                            jsonObject.getInt("cat_id"))),
                            (o1, o2) -> Integer.compare(o1.getParentId(), o2.getId()));
                }
            }
        }
        return new ConnectionResult<>(baseCategory);
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
        CategoryTree category = (CategoryTree) parameters.get("category");

        Map<String, Object> where = new HashMap<>();
        where.put("id", category.getValue().getId());

        Map<String, Object> values = new HashMap<>();
        values.put("name", category.getValue().getName());

        this.database.update(Database.Table.USERS_CATEGORIES, where, values);

        return new ConnectionResult<>(null);
    }

    public ConnectionResult<Void> deleteCategory(Logger logger, Map<String, Object> parameters) throws Exception {
        logger.log(Level.INFO, "Deleting category ...");
        CategoryTree category = (CategoryTree) parameters.get("category");

        Map<String, Object> where = new HashMap<>();
        where.put("id", category.getValue().getId());
        this.database.delete(Database.Table.USERS_CATEGORIES, where);

        where.clear();
        where.put("parent_id", category.getValue().getId());
        this.database.delete(Database.Table.USERS_CATEGORIES, where);

        where.clear();
        where.put("cat_id", category.getValue().getId());
        if (category.getCategoryClass().isFixed()) {
            this.database.delete(Database.Table.FIXED_TRANSACTIONS, where);
        } else {
            this.database.delete(Database.Table.TRANSACTIONS, where);
        }

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
            Category category = new Category(jsonObjectCategory.getInt("id"),
                    jsonObjectCategory.getString("name"),
                    (jsonObjectCategory.get("parent_id") == "null" ? -1 : jsonObjectCategory.getInt("parent_id")),
                    jsonObjectCategory.getInt("cat_id"));

            // TODO get real CategoryTree instance
            transactions.add(new Transaction(jsonObjectTransaction.getInt("id"),
                    jsonObjectTransaction.getDouble("amount"),
                    new CategoryTree(BaseCategory.CategoryClass.getCategoryClassByIndex(category.getRootId() - 1), null, category),
                    jsonObjectTransaction.getString("product"),
                    jsonObjectTransaction.getString("purpose"),
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
        values.put("cat_id", transaction.getCategoryTree().getValue().getId());

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
        values.put("cat_id", transaction.getCategoryTree().getValue().getId());

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
                    jsonObjectCategory.getString("name"),
                    (jsonObjectCategory.get("parent_id") == "null" ? -1 : jsonObjectCategory.getInt("parent_id")),
                    jsonObjectCategory.getInt("cat_id"));

            // fetching respective transaction amounts if the flag "is_variable" is true
            List<TransactionAmount> transactionAmounts = new ArrayList<>();
            if (jsonObjectTransaction.getInt("is_variable") == 1) {
                whereClause.clear();
                whereClause.put("fixed_transaction_id", jsonObjectTransaction.getInt("id"));
                JSONArray jsonArrayTransactionAmount = this.database.get(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, whereClause,
                        "value_date DESC");
                for (int j = 0; j < jsonArrayTransactionAmount.length(); j++) {
                    JSONObject jsonObjectTransactionAmount = jsonArrayTransactionAmount.getJSONObject(j);
                    transactionAmounts.add(new TransactionAmount(jsonObjectTransactionAmount.getInt("id"),
                            jsonObjectTransactionAmount.getDouble("amount"),
                            ((Date) jsonObjectTransactionAmount.get("value_date")).toLocalDate()));
                }
            }

            transactionAmounts.sort(Comparator.comparing(TransactionAmount::getValueDate).reversed());

            // TODO get real CategoryTree instance
            fixedTransactions.add(new FixedTransaction(jsonObjectTransaction.getInt("id"),
                    (jsonObjectTransaction.get("amount") == "null" ? 0 : jsonObjectTransaction.getDouble("amount")),
                    new CategoryTree(BaseCategory.CategoryClass.getCategoryClassByIndex(category.getRootId() - 1), null, category),
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
        whereParameters.put("cat_id", fixedTransaction.getCategoryTree().getValue().getId());
        whereParameters.put("end_date", null);

        Map<String, Object> values = new HashMap<>();
        values.put("end_date", LocalDate.now().toString());

        this.database.update(Database.Table.FIXED_TRANSACTIONS, whereParameters, values);

        values.clear();
        values.put("user_id", user.getId());
        values.put("amount", (fixedTransaction.isVariable() ? null : fixedTransaction.getAmount()));
        values.put("cat_id", fixedTransaction.getCategoryTree().getValue().getId());
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

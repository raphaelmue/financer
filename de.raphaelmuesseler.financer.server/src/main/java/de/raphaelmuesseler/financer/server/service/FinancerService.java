package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseToken;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
import de.raphaelmuesseler.financer.shared.model.user.Token;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class FinancerService {

    private static FinancerService INSTANCE = null;
    private RandomString tokenGenerator = new RandomString(64);

    private FinancerService() {
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
     * @param parameters [String token]
     * @return User that has this token
     */
    public synchronized User checkUsersToken(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Checking users token ...");

        User user = null;

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<DatabaseToken> criteriaQuery = criteriaBuilder.createQuery(DatabaseToken.class);
        Root<DatabaseToken> root = criteriaQuery.from(DatabaseToken.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("token"), parameters.get("token")));
        try {
            DatabaseToken databaseToken = session.createQuery(criteriaQuery).getSingleResult();
            if (databaseToken != null) {
                Token token = new Token(databaseToken);
                if (token.isValid()) {
                    logger.log(Level.INFO, "Token of user '" + token.getUser().getFullName() + "' is approved");
                    user = new User(session.get(DatabaseUser.class, token.getUser().getId()));

                    // needs to be called to avoid LazyInitializationException
                    user.getTokens().size();
                }
            }
        } catch (NoResultException ignored) {
        }

        transaction.commit();

        logger.log(Level.INFO, "Token is invalid");
        return user;
    }

    /**
     * Generates a new token (or updates the token, if the IP address is already store in the database)
     * and stores it in the database
     *
     * @param user      user
     * @param ipAddress ip address of client
     * @param system    operating system
     * @param isMobile  defines whether operating system is a mobile device
     */
    User generateToken(User user, String ipAddress, String system, boolean isMobile) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        user = new User(session.load(DatabaseUser.class, user.getId()));

        try {
            boolean foundEntry = false;
            for (DatabaseToken token : user.getTokens()) {
                if (token.getIpAddress().equals(ipAddress)) {
                    foundEntry = true;
                    token.setToken(this.tokenGenerator.nextString());
                    session.merge(token);
                    break;
                }
            }

            // insert if not found
            if (!foundEntry) {
                DatabaseToken databaseToken;
                databaseToken = new DatabaseToken();
                databaseToken.setUser(user);
                databaseToken.setToken(this.tokenGenerator.nextString());
                databaseToken.setIpAddress(ipAddress);
                databaseToken.setSystem(system);
                databaseToken.setExpireDate(LocalDate.now().plusMonths(1));
                databaseToken.setIsMobile(isMobile);
                databaseToken.setId((int) session.save(databaseToken));
                user.getTokens().add(databaseToken);
            }
        } finally {
            transaction.commit();
        }
        return user;
    }

    /**
     * Checks, if the users credentials are correct
     *
     * @param parameters [String email, String password, String ipAddress, String system, boolean isMobile]
     * @return User object, if credentials are correct, else null
     */
    public synchronized ConnectionResult<User> checkCredentials(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Checking credentials ...");

        User user = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<DatabaseUser> criteriaQuery = criteriaBuilder.createQuery(DatabaseUser.class);
        Root<DatabaseUser> root = criteriaQuery.from(DatabaseUser.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("email"), parameters.get("email")));
        try {
            user = new User(session.createQuery(criteriaQuery).getSingleResult());
            String password = Hash.create((String) parameters.get("password"), user.getSalt());
            if (password.equals(user.getPassword())) {
                logger.log(Level.INFO, "Credentials of user '" + user.getFullName() + "' are approved.");
            } else {
                user = null;
            }
        } catch (NoResultException ignored) {
        }

        transaction.commit();

        if (user != null) {
            user = this.generateToken(user, (String) parameters.get("ipAddress"), (String) parameters.get("system"),
                    parameters.containsKey("isMobile") && (boolean) parameters.get("isMobile"));
        } else {
            logger.log(Level.INFO, "Credentials are incorrect.");
        }

        return new ConnectionResult<>(user);
    }

    /**
     * Registers a new user and stores it into database.
     *
     * @param parameters [User user, String ipAddress, String system, boolean isMobile]
     * @return void
     */
    public ConnectionResult<User> registerUser(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Registering new user ...");
        User user = (User) parameters.get("user");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        user.setId((Integer) session.save(user.toDatabaseAccessObject()));
        transaction.commit();

        // creating new token and inserting it to database
        user = this.generateToken(user, (String) parameters.get("ipAddress"), (String) parameters.get("system"),
                parameters.containsKey("isMobile") && (boolean) parameters.get("isMobile"));

        return new ConnectionResult<>(user);
    }

    /**
     * Updates a password of a user.
     *
     * @param parameters [User user]
     * @return void
     */
    public ConnectionResult<Void> changePassword(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Changing Users Password ...");
        User user = (User) parameters.get("user");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.merge(user.toDatabaseAccessObject());
        transaction.commit();

        return new ConnectionResult<>(null);
    }

//    public ConnectionResult<List<Token>> getUsersTokens(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Fetching all tokens of user ...");
//        User user = (User) parameters.get("user");
//
//        Map<String, Object> whereParameters = new HashMap<>();
//        whereParameters.put("user_id", user.getId());
//
//        List<DatabaseObject> databaseObjects = this.database.getObject(Database.Table.USERS_TOKENS, Token.class, whereParameters);
//        List<Token> result = new ArrayList<>();
//
//        for (DatabaseObject databaseObject : databaseObjects) {
//            if (((Token) databaseObject).getExpireDate().compareTo(LocalDate.now()) >= 0) {
//                result.add((Token) databaseObject);
//            }
//        }
//
//        return new ConnectionResult<>(result);
//    }
//
//    public ConnectionResult<Void> deleteToken(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Deleting users token ...");
//
//        Map<String, Object> whereParameters = new HashMap<>();
//        whereParameters.put("id", parameters.get("tokenId"));
//        this.database.delete(Database.Table.USERS_TOKENS, whereParameters);
//
//        return new ConnectionResult<>(null);
//    }
//
//    private void getUsersSettings(Logger logger, User user) throws SQLException {
//        logger.log(Level.INFO, "Fetching users settings ...");
//
//        Map<String, Object> whereParameters = new HashMap<>();
//        whereParameters.put("user_id", user.getId());
//
//        JSONArray result = this.database.get(Database.Table.USERS_SETTINGS, whereParameters);
//        for (int i = 0; i < result.length(); i++) {
//            user.getDatabaseSettings().setValueByProperty(result.getJSONObject(i).getString("property"),
//                    result.getJSONObject(i).getString("value"));
//        }
//    }
//
//    public ConnectionResult<User> updateUsersSettings(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Updating users settings ...");
//        User user = (User) parameters.get("user");
//
//        Map<String, Object> whereParameters = new HashMap<>();
//        whereParameters.put("user_id", user.getId());
//        whereParameters.put("property", parameters.get("property"));
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("value", parameters.get("value"));
//
//        if (this.database.get(Database.Table.USERS_SETTINGS, whereParameters).length() > 0) {
//            this.database.update(Database.Table.USERS_SETTINGS, whereParameters, values);
//        } else {
//            values.putAll(whereParameters);
//            this.database.insert(Database.Table.USERS_SETTINGS, values);
//        }
//        user.getDatabaseSettings().setValueByProperty((String) parameters.get("property"), (String) parameters.get("value"));
//
//        return new ConnectionResult<>(user);
//    }
//
//    public ConnectionResult<BaseCategory> getUsersCategories(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Fetching users categories ...");
//
//        Map<String, Object> whereClause = new HashMap<>();
//        whereClause.put("user_id", ((User) parameters.get("user")).getId());
//
//        BaseCategory baseCategory = new BaseCategory();
//
//        for (BaseCategory.CategoryClass categoryClass : BaseCategory.CategoryClass.values()) {
//            whereClause.put("cat_id", categoryClass.getIndex());
//            JSONArray jsonArray = this.database.get(Database.Table.USERS_CATEGORIES, whereClause,
//                    "cat_id ASC, parent_id ASC");
//
//            for (int j = 0; j < jsonArray.length(); j++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(j);
//
//                if (jsonObject.get("parent_id").equals("null")) {
//                    baseCategory.getCategoryTreeByCategoryClass(categoryClass).getChildren().add(
//                            new CategoryTreeImpl(categoryClass, baseCategory.getCategoryTreeByCategoryClass(categoryClass),
//                                    new Category(jsonObject.getInt("id"),
//                                            jsonObject.getString("name"),
//                                            (jsonObject.get("parent_id") == "null" ? -1 : jsonObject.getInt("parent_id")),
//                                            jsonObject.getInt("cat_id"))));
//                } else {
//                    TreeUtil.insertByValue(baseCategory.getCategoryTreeByCategoryClass(categoryClass), new CategoryTreeImpl(categoryClass, null,
//                                    new Category(jsonObject.getInt("id"),
//                                            jsonObject.getString("name"),
//                                            jsonObject.getInt("parent_id"),
//                                            jsonObject.getInt("cat_id"))),
//                            (o1, o2) -> Integer.compare(o1.getParentId(), o2.getId()));
//                }
//            }
//        }
//        return new ConnectionResult<>(baseCategory);
//    }
//
//    public ConnectionResult<Category> addCategory(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Adding new category ...");
//        User user = (User) parameters.get("user");
//        Category category = (Category) parameters.get("category");
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("user_id", user.getId());
//        values.put("cat_id", category.getRootId());
//        if (category.getParentId() != -1) {
//            values.put("parent_id", category.getParentId());
//        }
//        values.put("name", category.getName());
//
//
//        this.database.insert(Database.Table.USERS_CATEGORIES, values);
//
//        return new ConnectionResult<>((Category) this.database.getObject(Database.Table.USERS_CATEGORIES, Category.class, values).get(0));
//    }
//
//    public ConnectionResult<Void> updateCategory(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Updating users categories ...");
//        CategoryTree category = (CategoryTree) parameters.get("category");
//
//        Map<String, Object> where = new HashMap<>();
//        where.put("id", category.getValue().getId());
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("name", category.getValue().getName());
//
//        this.database.update(Database.Table.USERS_CATEGORIES, where, values);
//
//        return new ConnectionResult<>(null);
//    }
//
//    public ConnectionResult<Void> deleteCategory(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Deleting category ...");
//        CategoryTreeImpl category = (CategoryTreeImpl) parameters.get("category");
//
//        Map<String, Object> where = new HashMap<>();
//        where.put("id", category.getValue().getId());
//        this.database.delete(Database.Table.USERS_CATEGORIES, where);
//
//        where.clear();
//        where.put("parent_id", category.getValue().getId());
//        this.database.delete(Database.Table.USERS_CATEGORIES, where);
//
//        where.clear();
//        where.put("cat_id", category.getValue().getId());
//        if (category.getCategoryClass().isFixed()) {
//            this.database.delete(Database.Table.FIXED_TRANSACTIONS, where);
//        } else {
//            this.database.delete(Database.Table.TRANSACTIONS, where);
//        }
//
//        return new ConnectionResult<>(null);
//    }
//
//    public ConnectionResult<List<VariableTransaction>> getTransactions(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Fetching users transaction ...");
//        List<VariableTransaction> transactions = new ArrayList<>();
//
//        Map<String, Object> whereClause = new HashMap<>();
//        whereClause.put("user_id", ((User) parameters.get("user")).getId());
//
//        JSONArray jsonArray = this.database.get(Database.Table.TRANSACTIONS, whereClause);
//        whereClause.clear();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject jsonObjectTransaction = jsonArray.getJSONObject(i);
//
//            whereClause.put("id", jsonObjectTransaction.getInt("cat_id"));
//            JSONObject jsonObjectCategory = this.database.get(Database.Table.USERS_CATEGORIES, whereClause).getJSONObject(0);
//            Category category = new Category(jsonObjectCategory.getInt("id"),
//                    jsonObjectCategory.getString("name"),
//                    (jsonObjectCategory.get("parent_id") == "null" ? -1 : jsonObjectCategory.getInt("parent_id")),
//                    jsonObjectCategory.getInt("cat_id"));
//
//            // TODO get real CategoryTreeImpl instance
//            VariableTransaction transaction = new VariableTransaction(jsonObjectTransaction.getInt("id"),
//                    jsonObjectTransaction.getDouble("amount"),
//                    new CategoryTreeImpl(BaseCategory.CategoryClass.getCategoryClassByIndex(category.getRootId() - 1), null, category),
//                    jsonObjectTransaction.getString("product"),
//                    jsonObjectTransaction.getString("purpose"),
//                    ((Date) jsonObjectTransaction.get("value_date")).toLocalDate(),
//                    jsonObjectTransaction.getString("shop"));
//
//            whereClause.clear();
//            whereClause.put("transaction_id", transaction.getId());
//            for (DatabaseObject databaseObject : this.database.getObject(Database.Table.TRANSACTIONS_ATTACHMENTS,
//                    Attachment.class, whereClause, "id, transaction_id, name, upload_date")) {
//                transaction.getDatabaseAttachments().add((AttachmentWithContent) new AttachmentWithContent().fromDatabaseObject(databaseObject));
//            }
//
//            transactions.add(transaction);
//
//            whereClause.clear();
//        }
//        return new ConnectionResult<>(transactions);
//    }
//
//    public ConnectionResult<VariableTransaction> addTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Adding transaction ...");
//        User user = (User) parameters.get("user");
//        VariableTransaction transaction = (VariableTransaction) parameters.get("transaction");
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("user_id", user.getId());
//        values.put("value_date", transaction.getValueDate());
//        values.put("amount", transaction.getAmount());
//        values.put("product", transaction.getProduct());
//        values.put("purpose", transaction.getPurpose());
//        values.put("shop", transaction.getShop());
//        values.put("cat_id", transaction.getCategoryTree().getValue().getId());
//
//        this.database.insert(Database.Table.TRANSACTIONS, values);
//
//        transaction.setId(this.database.getLatestId(Database.Table.TRANSACTIONS));
//
//        return new ConnectionResult<>(transaction);
//    }
//
//
//    public ConnectionResult<Void> updateTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Adding transaction ...");
//        VariableTransaction transaction = (VariableTransaction) parameters.get("transaction");
//
//        Map<String, Object> where = new HashMap<>();
//        where.put("id", transaction.getId());
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("value_date", transaction.getValueDate());
//        values.put("amount", transaction.getAmount());
//        values.put("product", transaction.getProduct());
//        values.put("purpose", transaction.getPurpose());
//        values.put("shop", transaction.getShop());
//        values.put("cat_id", transaction.getCategoryTree().getValue().getId());
//
//        this.database.update(Database.Table.TRANSACTIONS, where, values);
//
//        return new ConnectionResult<>(null);
//    }
//
//    public ConnectionResult<AttachmentWithContent> uploadTransactionAttachment(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Uploading AttachmentWithContent ...");
//        AttachmentWithContent result = new AttachmentWithContent();
//        File attachmentFile = (File) parameters.get("attachmentFile");
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("transaction_id", ((VariableTransaction) parameters.get("transaction")).getId());
//        values.put("name", attachmentFile.getName());
//        values.put("upload_date", LocalDate.now().toString());
//        values.put("content", parameters.get("content"));
//
//        this.database.insert(Database.Table.TRANSACTIONS_ATTACHMENTS, values);
//
//        result.setId(this.database.getLatestId(Database.Table.TRANSACTIONS_ATTACHMENTS));
//        result.setName(attachmentFile.getName());
//        result.setUploadDate(LocalDate.now());
//        //result.setContent((byte[]) parameters.get("content"));
//
//        return new ConnectionResult<>(result);
//    }
//
//    public ConnectionResult<AttachmentWithContent> getAttachment(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Fetching attachment ...");
//        Map<String, Object> whereParameters = new HashMap<>();
//        whereParameters.put("id", parameters.get("id"));
//
//        List<DatabaseObject> result = this.database.getObject(Database.Table.TRANSACTIONS_ATTACHMENTS, AttachmentWithContent.class, whereParameters);
//        if (result != null && result.size() > 0) {
//            return new ConnectionResult<>((AttachmentWithContent) result.get(0));
//        }
//
//        return new ConnectionResult<>(null);
//    }
//
//    public ConnectionResult<Void> deleteAttachment(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Deleting attachment ...");
//        Map<String, Object> whereParamters = new HashMap<>();
//        whereParamters.put("id", parameters.get("id"));
//
//        this.database.delete(Database.Table.TRANSACTIONS_ATTACHMENTS, whereParamters);
//
//        return new ConnectionResult<>(null);
//    }
//
//    public ConnectionResult<Void> deleteTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Adding transaction ...");
//        VariableTransaction transaction = (VariableTransaction) parameters.get("transaction");
//
//        Map<String, Object> where = new HashMap<>();
//        where.put("id", transaction.getId());
//
//        this.database.delete(Database.Table.TRANSACTIONS, where);
//
//        return new ConnectionResult<>(null);
//    }
//
//    public ConnectionResult<List<FixedTransaction>> getFixedTransactions(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Fetching fixed transactions ...");
//
//        Map<String, Object> whereClause = new HashMap<>();
//        whereClause.put("user_id", ((User) parameters.get("user")).getId());
//
//        List<FixedTransaction> fixedTransactions = new ArrayList<>();
//
//        JSONArray jsonArray = this.database.get(Database.Table.FIXED_TRANSACTIONS, whereClause, "start_date DESC");
//        for (int i = 0; i < jsonArray.length(); i++) {
//            whereClause.clear();
//
//            JSONObject jsonObjectTransaction = jsonArray.getJSONObject(i);
//
//            // fetching category object
//            whereClause.put("id", jsonObjectTransaction.getInt("cat_id"));
//            JSONObject jsonObjectCategory = this.database.get(Database.Table.USERS_CATEGORIES, whereClause).getJSONObject(0);
//            whereClause.clear();
//            Category category = new Category(jsonObjectCategory.getInt("id"),
//                    jsonObjectCategory.getString("name"),
//                    (jsonObjectCategory.get("parent_id") == "null" ? -1 : jsonObjectCategory.getInt("parent_id")),
//                    jsonObjectCategory.getInt("cat_id"));
//
//            // fetching respective transaction amounts if the flag "is_variable" is true
//            List<TransactionAmount> transactionAmounts = new ArrayList<>();
//            if (jsonObjectTransaction.getInt("is_variable") == 1) {
//                whereClause.clear();
//                whereClause.put("fixed_transaction_id", jsonObjectTransaction.getInt("id"));
//                JSONArray jsonArrayTransactionAmount = this.database.get(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, whereClause,
//                        "value_date DESC");
//                for (int j = 0; j < jsonArrayTransactionAmount.length(); j++) {
//                    JSONObject jsonObjectTransactionAmount = jsonArrayTransactionAmount.getJSONObject(j);
//                    transactionAmounts.add(new TransactionAmount(jsonObjectTransactionAmount.getInt("id"),
//                            jsonObjectTransactionAmount.getDouble("amount"),
//                            ((Date) jsonObjectTransactionAmount.get("value_date")).toLocalDate()));
//                }
//            }
//
//            transactionAmounts.sort(Comparator.comparing(TransactionAmount::getValueDate).reversed());
//
//            // TODO get real CategoryTreeImpl instance
//            fixedTransactions.add(new FixedTransaction(jsonObjectTransaction.getInt("id"),
//                    (jsonObjectTransaction.get("amount") == "null" ? 0 : jsonObjectTransaction.getDouble("amount")),
//                    new CategoryTreeImpl(BaseCategory.CategoryClass.getCategoryClassByIndex(category.getRootId() - 1), null, category),
//                    (jsonObjectTransaction.get("product") == "null" ? null : jsonObjectTransaction.getString("product")),
//                    (jsonObjectTransaction.get("purpose") == "null" ? null : jsonObjectTransaction.getString("purpose")),
//                    ((Date) jsonObjectTransaction.get("start_date")).toLocalDate(),
//                    (jsonObjectTransaction.get("end_date") == "null" ? null : ((Date) jsonObjectTransaction.get("end_date")).toLocalDate()),
//                    (jsonObjectTransaction.getInt("is_variable") == 1),
//                    jsonObjectTransaction.getInt("day"),
//                    transactionAmounts));
//        }
//
//        return new ConnectionResult<>(fixedTransactions);
//    }
//
//    public ConnectionResult<FixedTransaction> addFixedTransactions(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Adding fixed transactions ...");
//        User user = (User) parameters.get("user");
//        FixedTransaction fixedTransaction = (FixedTransaction) parameters.get("fixedTransaction");
//
//        Map<String, Object> whereParameters = new HashMap<>();
//        whereParameters.put("user_id", user.getId());
//        whereParameters.put("cat_id", fixedTransaction.getCategoryTree().getValue().getId());
//        whereParameters.put("end_date", null);
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("end_date", fixedTransaction.getStartDate());
//
//        this.database.update(Database.Table.FIXED_TRANSACTIONS, whereParameters, values);
//
//        values.clear();
//        values.put("user_id", user.getId());
//        values.put("amount", (fixedTransaction.isVariable() ? null : fixedTransaction.getAmount()));
//        values.put("cat_id", fixedTransaction.getCategoryTree().getValue().getId());
//        values.put("start_date", fixedTransaction.getStartDate());
//        values.put("end_date", fixedTransaction.getEndDate());
//        values.put("is_variable", (fixedTransaction.isVariable() ? 1 : 0));
//        values.put("day", fixedTransaction.getDay());
//
//        this.database.insert(Database.Table.FIXED_TRANSACTIONS, values);
//
//        fixedTransaction.setId(this.database.getLatestId(Database.Table.FIXED_TRANSACTIONS));
//        this.updateOrCreateTransactionAmounts(fixedTransaction);
//
//        return new ConnectionResult<>(fixedTransaction);
//    }
//
//
//    public ConnectionResult<Void> updateFixedTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Updating fixed transaction ...");
//        FixedTransaction fixedTransaction = (FixedTransaction) parameters.get("fixedTransaction");
//
//        Map<String, Object> whereParameters = new HashMap<>();
//        whereParameters.put("id", fixedTransaction.getId());
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("amount", (fixedTransaction.isVariable() ? null : fixedTransaction.getAmount()));
//        values.put("start_date", fixedTransaction.getStartDate());
//        values.put("end_date", fixedTransaction.getEndDate());
//        values.put("is_variable", (fixedTransaction.isVariable() ? 1 : 0));
//        values.put("day", fixedTransaction.getDay());
//
//        this.database.update(Database.Table.FIXED_TRANSACTIONS, whereParameters, values);
//
//        this.updateOrCreateTransactionAmounts(fixedTransaction);
//
//        return new ConnectionResult<>(null);
//    }
//
//    public ConnectionResult<Void> deleteFixedTransaction(Logger logger, Map<String, Object> parameters) throws Exception {
//        logger.log(Level.INFO, "Deleting fixed transaction ...");
//        Map<String, Object> whereParameters = new HashMap<>();
//        whereParameters.put("id", ((FixedTransaction) parameters.get("fixedTransaction")).getId());
//        this.database.delete(Database.Table.FIXED_TRANSACTIONS, whereParameters);
//
//        whereParameters.clear();
//        whereParameters.put("fixed_transaction_id", ((FixedTransaction) parameters.get("fixedTransaction")).getId());
//        this.database.delete(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, whereParameters);
//        return new ConnectionResult<>(null);
//    }
//
//    private void updateOrCreateTransactionAmounts(FixedTransaction fixedTransaction) throws SQLException {
//        Map<String, Object> whereParameters = new HashMap<>();
//        Map<String, Object> values = new HashMap<>();
//
//        if (fixedTransaction.isVariable() && fixedTransaction.getTransactionAmounts() != null) {
//            for (TransactionAmount transactionAmount : fixedTransaction.getTransactionAmounts()) {
//                whereParameters.put("id", transactionAmount.getId());
//
//                values.put("fixed_transaction_id", fixedTransaction.getId());
//                values.put("value_date", transactionAmount.getValueDate());
//                values.put("amount", transactionAmount.getAmount());
//                if (transactionAmount.getId() < 0) {
//                    this.database.insert(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, values);
//                } else {
//                    this.database.update(Database.Table.FIXED_TRANSACTIONS_AMOUNTS, whereParameters, values);
//                }
//            }
//        }
//    }
}

package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.shared.model.db.*;
import de.raphaelmuesseler.financer.shared.model.transactions.Attachment;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.Token;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
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
        CriteriaQuery<TokenDAO> criteriaQuery = criteriaBuilder.createQuery(TokenDAO.class);
        Root<TokenDAO> root = criteriaQuery.from(TokenDAO.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("token"), parameters.get("token")));
        try {
            TokenDAO databaseToken = session.createQuery(criteriaQuery).getSingleResult();
            if (databaseToken != null) {
                Token token = new Token(databaseToken);
                if (token.isValid()) {
                    logger.log(Level.INFO, "Token of user '" + token.getUser().getFullName() + "' is approved");
                    user = new User(session.get(UserDAO.class, token.getUser().getId()));

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
    synchronized User generateToken(User user, String ipAddress, String system, boolean isMobile) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        user = new User(session.load(UserDAO.class, user.getId()));

        try {
            boolean foundEntry = false;
            for (TokenDAO token : user.getTokens()) {
                if (token.getIpAddress().equals(ipAddress)) {
                    foundEntry = true;
                    token.setToken(this.tokenGenerator.nextString());
                    session.merge(token);
                    break;
                }
            }

            // insert if not found
            if (!foundEntry) {
                TokenDAO databaseToken;
                databaseToken = new TokenDAO();
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
        CriteriaQuery<UserDAO> criteriaQuery = criteriaBuilder.createQuery(UserDAO.class);
        Root<UserDAO> root = criteriaQuery.from(UserDAO.class);
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
    public synchronized ConnectionResult<User> registerUser(Logger logger, Map<String, Object> parameters) {
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
    public synchronized ConnectionResult<Void> changePassword(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Changing Users Password ...");
        User user = (User) parameters.get("user");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.merge(user.toDatabaseAccessObject());
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Deletes a token of a user.
     *
     * @param parameters [int tokenId]
     * @return void
     */
    public synchronized ConnectionResult<Void> deleteToken(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Deleting users token ...");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        TokenDAO token = session.load(TokenDAO.class, (int) parameters.get("tokenId"));
        session.delete(token);
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Updates the settings of a user
     *
     * @param parameters [User user]
     * @return void
     */
    public synchronized ConnectionResult<Void> updateUsersSettings(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Updating users settings ...");
        User user = (User) parameters.get("user");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        for (SettingsDAO databaseSettings : user.getDatabaseSettings()) {
            session.saveOrUpdate(databaseSettings);
        }

        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Returns the users categories as BaseCategory
     *
     * @param parameters [int userId]
     * @return BaseCategory object
     */
    public synchronized ConnectionResult<BaseCategory> getUsersCategories(Logger logger, Map<String, Object> parameters) throws IllegalArgumentException {
        logger.log(Level.INFO, "Fetching users categories ...");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        User user = new User(session.get(UserDAO.class, (int) parameters.get("userId")));

        List<CategoryDAO> categories = new ArrayList<>(user.getCategories());
        Collections.sort(categories);

        BaseCategory baseCategory = new BaseCategory();

        for (CategoryDAO databaseCategory : categories) {
            Category category = new Category(databaseCategory);
            if (databaseCategory.getParentId() == -1) {
                baseCategory.getCategoryTreeByCategoryClass(category.getCategoryClass()).getChildren().add(new CategoryTreeImpl(category));
            } else {
                if (!TreeUtil.insertByValue(baseCategory.getCategoryTreeByCategoryClass(category.getCategoryClass()), new CategoryTreeImpl(new Category(databaseCategory)),
                        (o1, o2) -> Integer.compare(o1.getParentId(), o2.getId()))) {
                    throw new IllegalArgumentException("Category \"" + category.getName() + "\" could not be inserted into CategoryTree");
                }
            }
        }
        transaction.commit();
        return new ConnectionResult<>(baseCategory);
    }

    /**
     * Adds a new category.
     *
     * @param parameters [User user, Category category]
     * @return Category object
     */
    public synchronized ConnectionResult<Category> addCategory(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding new category ...");
        User user = (User) parameters.get("user");
        Category category = (Category) parameters.get("category");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        category.setId((int) session.save(category.toDatabaseAccessObject()));
        transaction.commit();

        return new ConnectionResult<>(category);
    }

    /**
     * Updates a category.
     *
     * @param parameters [Category category]
     * @return void
     */
    public synchronized ConnectionResult<Void> updateCategory(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Updating users categories ...");
        Category category = (Category) parameters.get("category");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(category.toDatabaseAccessObject());
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Deletes a category and all its children as well as all transactions.
     *
     * @param parameters [Category category]
     * @return void
     */
    public synchronized ConnectionResult<Void> deleteCategory(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Deleting category ...");
        Category category = (Category) parameters.get("category");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.delete(category.toDatabaseAccessObject());
        transaction.commit();

        this.deleteCategoryChildren(category);

        session = HibernateUtil.getSessionFactory().getCurrentSession();
        transaction = session.beginTransaction();
        session.createQuery("delete from FixedTransactionDAO where category = :categoryId")
                .setParameter("categoryId", category.getId());
        session.createQuery("delete from VariableTransactionDAO where category = :categoryId")
                .setParameter("categoryId", category.getId());
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Deletes all children of a given category.
     *
     * @param category category to delete children
     */
    private synchronized void deleteCategoryChildren(Category category) {
        List<CategoryDAO> categories;
        int parentId = category.getId();
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        categories = session.createQuery("from CategoryDAO where parentId = :parentId", CategoryDAO.class)
                .setParameter("parentId", parentId).list();
        transaction.commit();
        if (categories.size() > 0) {
            for (CategoryDAO databaseCategory : categories) {
                session = HibernateUtil.getSessionFactory().getCurrentSession();
                transaction = session.beginTransaction();
                session.delete(databaseCategory);
                transaction.commit();
                deleteCategoryChildren(new Category(databaseCategory));
            }
        }
    }

    /**
     * Returns all transactions of a user.
     *
     * @param parameters [int userId, BaseCategory baseCategory]
     * @return BaseCategory with transactions
     */
    public synchronized ConnectionResult<BaseCategory> getTransactions(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Fetching users transaction ...");

        BaseCategory baseCategory = (BaseCategory) parameters.get("baseCategory");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        baseCategory.traverse(treeObject -> {
            CategoryTree categoryTree = (CategoryTree) treeObject;
            if (!categoryTree.getValue().getCategoryClass().isFixed()) {
                List<VariableTransactionDAO> databaseVariableTransactions = session
                        .createQuery("from VariableTransactionDAO where category = :category",
                                VariableTransactionDAO.class)
                        .setParameter("category", treeObject.getValue().toDatabaseAccessObject())
                        .list();
                Set<VariableTransaction> variableTransactions = new HashSet<>();
                for (VariableTransactionDAO databaseVariableTransaction : databaseVariableTransactions) {
                    variableTransactions.add(new VariableTransaction(databaseVariableTransaction, categoryTree));
                }
                categoryTree.getTransactions().addAll(variableTransactions);
            }
        });

        transaction.commit();
        return new ConnectionResult<>(baseCategory);
    }

    /**
     * Adds a new transaction.
     *
     * @param parameters [VariableTransaction variableTransaction]
     * @return variable transaction object
     */
    public synchronized ConnectionResult<VariableTransaction> addTransaction(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding transaction ...");
        VariableTransaction variableTransaction = (VariableTransaction) parameters.get("variableTransaction");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        variableTransaction.setId((int) session.save(variableTransaction.toDatabaseAccessObject()));
        transaction.commit();

        return new ConnectionResult<>(variableTransaction);
    }

    /**
     * Updates a variable transaction.
     *
     * @param parameters [VariableTransaction variableTransaction]
     * @return void
     */
    public synchronized ConnectionResult<Void> updateTransaction(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding transaction ...");
        VariableTransaction variableTransaction = (VariableTransaction) parameters.get("variableTransaction");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(variableTransaction.toDatabaseAccessObject());
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Deletes a transaction.
     *
     * @param parameters [int variableTransactionId]
     * @return
     */
    public ConnectionResult<Void> deleteTransaction(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding transaction ...");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.createQuery("delete from VariableTransactionDAO where id = :variableTransactionId")
                .setParameter("variableTransactionId", parameters.get("variableTransactionId"))
                .executeUpdate();
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Uploads a transaction attachment to the database
     *
     * @param parameters [File attachmentFile, VariableTransaction transaction, byte[] content]
     * @return Attachment object
     */
    public synchronized ConnectionResult<Attachment> uploadTransactionAttachment(Logger logger, Map<String, Object> parameters) throws SQLException {
        logger.log(Level.INFO, "Uploading AttachmentWithContent ...");
        Attachment result = new Attachment();
        File attachmentFile = (File) parameters.get("attachmentFile");

        result.setTransaction((VariableTransaction) parameters.get("transaction"));
        result.setName(attachmentFile.getName());
        result.setUploadDate(LocalDate.now());
        result.setContent((byte[]) parameters.get("content"));

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        result.setId((int) session.save(result.toDatabaseAccessObject()));
        transaction.commit();

        return new ConnectionResult<>(new Attachment(result));
    }

    /**
     * Returns an attachment with content.
     *
     * @param parameters [int attachmentId]
     * @return Attachment object or null, if none found
     */
    public ConnectionResult<Attachment> getAttachment(Logger logger, Map<String, Object> parameters) throws SQLException {
        logger.log(Level.INFO, "Fetching attachment ...");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        TransactionAttachmentDAO databaseAttachment = session.get(TransactionAttachmentDAO.class, (int) parameters.get("attachmentId"));
        Attachment attachment = null;
        if (databaseAttachment != null) {
            attachment = new Attachment(databaseAttachment);
        }
        transaction.commit();

        return new ConnectionResult<>(attachment);
    }

    /**
     * Deletes a attachment.
     *
     * @param parameters [int attachmentId]
     * @return void
     */
    public ConnectionResult<Void> deleteAttachment(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Deleting attachment ...");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.createQuery("delete from TransactionAttachmentDAO where id = :attachmentId")
                .setParameter("attachmentId", parameters.get("attachmentId"));
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Returns a BaseCategory object with all fixed transactions.
     *
     * @param parameters [int userId, BaseCategory baseCategory]
     * @return BaseCategory object with fixed transactions
     */
    public ConnectionResult<BaseCategory> getFixedTransactions(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Fetching fixed transactions ...");

        BaseCategory baseCategory = (BaseCategory) parameters.get("baseCategory");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        baseCategory.traverse(treeObject -> {
            CategoryTree categoryTree = (CategoryTree) treeObject;
            if (categoryTree.getValue().getCategoryClass().isFixed()) {
                List<FixedTransactionDAO> databaseFixedTransactions = session
                        .createQuery("from FixedTransactionDAO where category = :category",
                                FixedTransactionDAO.class)
                        .setParameter("category", treeObject.getValue().toDatabaseAccessObject())
                        .list();
                Set<FixedTransaction> variableTransactions = new HashSet<>();
                for (FixedTransactionDAO databaseFixedTransaction : databaseFixedTransactions) {
                    variableTransactions.add(new FixedTransaction(databaseFixedTransaction, categoryTree));
                }
                categoryTree.getTransactions().addAll(variableTransactions);
            }
        });

        transaction.commit();

        return new ConnectionResult<>(baseCategory);
    }

    /**
     * Adds a new fixed transaction and stops the old.
     *
     * @param parameters [FixedTransaction fixedTransaction]
     * @return FixedTransaction object
     */
    public ConnectionResult<FixedTransaction> addFixedTransactions(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding fixed transactions ...");
        FixedTransaction fixedTransaction = (FixedTransaction) parameters.get("fixedTransaction");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        FixedTransactionDAO oldFixedTransaction = session.createQuery("from FixedTransactionDAO where category = :categoryId " +
                "and endDate = null ", FixedTransactionDAO.class)
                .setParameter("categoryId", fixedTransaction.getCategoryTree().getValue())
                .uniqueResult();
        oldFixedTransaction.setEndDate(LocalDate.now());
        session.update(oldFixedTransaction);

        fixedTransaction.setId((int) session.save(fixedTransaction.toDatabaseAccessObject()));

        transaction.commit();

        return new ConnectionResult<>(fixedTransaction);
    }

    /**
     * Updates a fixed transaction.
     *
     * @param parameters [FixedTransaction fixedTransaction]
     * @return void
     */
    public ConnectionResult<Void> updateFixedTransaction(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Updating fixed transaction ...");
        FixedTransaction fixedTransaction = (FixedTransaction) parameters.get("fixedTransaction");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(fixedTransaction.toDatabaseAccessObject());
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Deletes a fixed transaction.
     *
     * @param parameters [int fixedTransactionId]
     * @return void
     */
    public ConnectionResult<Void> deleteFixedTransaction(Logger logger, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Deleting fixed transaction ...");

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.createQuery("delete from FixedTransactionDAO where id = :fixedTransactionId")
                .setParameter("fixedTransactionId", parameters.get("fixedTransactionId"))
                .executeUpdate();
        transaction.commit();

        return new ConnectionResult<>(null);
    }
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

package de.raphaelmuesseler.financer.server.service;

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
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public User checkUsersToken(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Checking users token ...");

        User user = null;

        Transaction transaction = session.beginTransaction();
        try {
            TokenDAO databaseToken = session.createQuery("from TokenDAO where token = :token", TokenDAO.class)
                    .setParameter("token", parameters.get("token")).getSingleResult();
            if (databaseToken != null) {
                Token token = new Token(databaseToken);
                if (token.isValid()) {
                    logger.log(Level.INFO, "Token of user '" + token.getUser().getFullName() + "' is approved");
                    user = new User(session.get(UserDAO.class, token.getUser().getId()));

                    // needs to be called to avoid LazyInitializationException
                    user.setActiveToken(token);
                    user.getTokens().size();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        transaction.commit();

        if (user == null) {
            logger.log(Level.INFO, "Token is invalid");
        }
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
    User generateToken(Session session, User user, String ipAddress, String system, boolean isMobile) {
        Transaction transaction = session.beginTransaction();
        user = new User(session.get(UserDAO.class, user.getId()));

        try {
            boolean foundEntry = false;
            for (TokenDAO token : user.getTokens()) {
                if (token.getIpAddress().equals(ipAddress)) {
                    foundEntry = true;
                    token.setToken(this.tokenGenerator.nextString());
                    session.merge(token);
                    user.setActiveToken(new Token(token));
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
                user.setActiveToken(new Token(databaseToken));
            }

            if (user.getDatabaseSettings() != null) {
                user.getDatabaseSettings().size();
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
    public ConnectionResult<User> checkCredentials(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Checking credentials ...");

        User user = null;
        Transaction transaction = session.beginTransaction();

        try {
            user = new User(session.createQuery("from UserDAO where email = :email", UserDAO.class)
                    .setParameter("email", parameters.get("email")).getSingleResult());
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
            user = this.generateToken(session, user, (String) parameters.get("ipAddress"), (String) parameters.get("system"),
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
    public ConnectionResult<User> registerUser(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Registering new user ...");
        User user = (User) parameters.get("user");

        Transaction transaction = session.beginTransaction();
        user.setId((Integer) session.save(user.toDatabaseAccessObject()));
        transaction.commit();

        // creating new token and inserting it to database
        user = this.generateToken(session, user, (String) parameters.get("ipAddress"), (String) parameters.get("system"),
                parameters.containsKey("isMobile") && (boolean) parameters.get("isMobile"));

        return new ConnectionResult<>(user);
    }

    /**
     * Updates a password of a user.
     *
     * @param parameters [User user]
     * @return void
     */
    public ConnectionResult<Void> changePassword(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Changing Users Password ...");
        User user = (User) parameters.get("user");

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
    public ConnectionResult<Void> deleteToken(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Deleting users token ...");

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
    public ConnectionResult<Void> updateUsersSettings(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Updating users settings ...");
        User user = (User) parameters.get("user");

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
    public ConnectionResult<BaseCategory> getUsersCategories(Logger logger, Session session, Map<String, Object> parameters) throws IllegalArgumentException {
        logger.log(Level.INFO, "Fetching users categories ...");
        BaseCategory baseCategory;

        Transaction transaction = session.beginTransaction();
        User user = new User(session.get(UserDAO.class, (int) parameters.get("userId")));
        List<CategoryDAO> categories = new ArrayList<>(user.getCategories());
        Collections.sort(categories);

        baseCategory = new BaseCategory();

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
     * @param parameters [Category category]
     * @return Category object
     */
    public ConnectionResult<Category> addCategory(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding new category ...");
        Category category = (Category) parameters.get("category");

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
    public ConnectionResult<Void> updateCategory(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Updating users categories ...");
        Category category = (Category) parameters.get("category");

        Transaction transaction = session.beginTransaction();
        session.update(category.toDatabaseAccessObject());
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Deletes a category and all its children as well as all transactions.
     *
     * @param parameters [int categoryId]
     * @return void
     */
    public ConnectionResult<Void> deleteCategory(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Deleting category ...");
        int categoryId = (int) parameters.get("categoryId");

        Transaction transaction = session.beginTransaction();
        session.createQuery("delete from CategoryDAO where id = :categoryId")
                .setParameter("categoryId", categoryId).executeUpdate();
        transaction.commit();

        this.deleteCategoryChildren(session, categoryId);

        transaction = session.beginTransaction();
        session.createQuery("delete from FixedTransactionDAO where category.id = :categoryId")
                .setParameter("categoryId", categoryId);
        session.createQuery("delete from VariableTransactionDAO where category.id = :categoryId")
                .setParameter("categoryId", categoryId);
        transaction.commit();

        return new ConnectionResult<>(null);
    }

    /**
     * Deletes all children of a given category.
     *
     * @param categoryId category id to delete children
     */
    private void deleteCategoryChildren(Session session, int categoryId) {
        List<CategoryDAO> categories;

        Transaction transaction = session.beginTransaction();
        categories = session.createQuery("from CategoryDAO where parentId = :parentId", CategoryDAO.class)
                .setParameter("parentId", categoryId).list();
        transaction.commit();
        if (categories.size() > 0) {
            for (CategoryDAO databaseCategory : categories) {
                transaction = session.beginTransaction();
                session.delete(databaseCategory);
                transaction.commit();
                deleteCategoryChildren(session, databaseCategory.getId());
            }
        }
    }

    /**
     * Returns all transactions of a user.
     *
     * @param parameters [int userId, BaseCategory baseCategory]
     * @return BaseCategory with transactions
     */
    public ConnectionResult<BaseCategory> getTransactions(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Fetching users transaction ...");
        BaseCategory baseCategory;
        baseCategory = (BaseCategory) parameters.get("baseCategory");

        Transaction transaction = session.beginTransaction();
        baseCategory.traverse(treeObject -> {
            CategoryTree categoryTree = (CategoryTree) treeObject;
            if (!categoryTree.getValue().getCategoryClass().isFixed()) {
                List<VariableTransactionDAO> databaseVariableTransactions = session
                        .createQuery("from VariableTransactionDAO where category.id = :categoryId",
                                VariableTransactionDAO.class)
                        .setParameter("categoryId", treeObject.getValue().getId())
                        .list();
                for (VariableTransactionDAO databaseVariableTransaction : databaseVariableTransactions) {
                    categoryTree.getTransactions().add(new VariableTransaction(databaseVariableTransaction, categoryTree));
                }
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
    public ConnectionResult<VariableTransaction> addTransaction(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding transaction ...");
        VariableTransaction variableTransaction = (VariableTransaction) parameters.get("variableTransaction");

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
    public ConnectionResult<Void> updateTransaction(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding transaction ...");
        VariableTransaction variableTransaction = (VariableTransaction) parameters.get("variableTransaction");

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
    public ConnectionResult<Void> deleteTransaction(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding transaction ...");

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
    public ConnectionResult<Attachment> uploadTransactionAttachment(Logger logger, Session session, Map<String, Object> parameters) throws SQLException {
        logger.log(Level.INFO, "Uploading AttachmentWithContent ...");
        Attachment result = new Attachment();
        File attachmentFile = (File) parameters.get("attachmentFile");

        result.setTransaction((VariableTransaction) parameters.get("transaction"));
        result.setName(attachmentFile.getName());
        result.setUploadDate(LocalDate.now());
        result.setContent((byte[]) parameters.get("content"));

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
    public ConnectionResult<Attachment> getAttachment(Logger logger, Session session, Map<String, Object> parameters) throws SQLException {
        logger.log(Level.INFO, "Fetching attachment ...");

        Attachment attachment = null;
        Transaction transaction = session.beginTransaction();
        TransactionAttachmentDAO databaseAttachment = session.get(TransactionAttachmentDAO.class, (int) parameters.get("attachmentId"));
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
    public ConnectionResult<Void> deleteAttachment(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Deleting attachment ...");

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
    public ConnectionResult<BaseCategory> getFixedTransactions(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Fetching fixed transactions ...");
        BaseCategory baseCategory;
        baseCategory = (BaseCategory) parameters.get("baseCategory");

        Transaction transaction = session.beginTransaction();
        baseCategory.traverse(treeObject -> {
            CategoryTree categoryTree = (CategoryTree) treeObject;
            if (categoryTree.getValue().getCategoryClass().isFixed()) {
                List<FixedTransactionDAO> databaseFixedTransactions = session
                        .createQuery("from FixedTransactionDAO where category.id = :categoryId",
                                FixedTransactionDAO.class)
                        .setParameter("categoryId", treeObject.getValue().getId())
                        .list();
                for (FixedTransactionDAO databaseFixedTransaction : databaseFixedTransactions) {
                    categoryTree.getTransactions().add(new FixedTransaction(databaseFixedTransaction, categoryTree));
                }
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
    public synchronized ConnectionResult<FixedTransaction> addFixedTransactions(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Adding fixed transactions ...");
        FixedTransaction fixedTransaction = (FixedTransaction) parameters.get("fixedTransaction");

        Transaction transaction = session.beginTransaction();
        FixedTransactionDAO oldFixedTransaction = session.createQuery("from FixedTransactionDAO where category.id = :categoryId " +
                "and endDate = null ", FixedTransactionDAO.class)
                .setParameter("categoryId", fixedTransaction.getCategoryTree().getValue().getId())
                .uniqueResult();
        if (oldFixedTransaction != null) {
            oldFixedTransaction.setEndDate(LocalDate.now());
            session.update(oldFixedTransaction);
        }
        transaction.commit();
        transaction = session.beginTransaction();
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
    public ConnectionResult<Void> updateFixedTransaction(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Updating fixed transaction ...");
        FixedTransaction fixedTransaction = (FixedTransaction) parameters.get("fixedTransaction");

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
    public ConnectionResult<Void> deleteFixedTransaction(Logger logger, Session session, Map<String, Object> parameters) {
        logger.log(Level.INFO, "Deleting fixed transaction ...");

        Transaction transaction = session.beginTransaction();
        session.createQuery("delete from FixedTransactionDAO where id = :fixedTransactionId")
                .setParameter("fixedTransactionId", parameters.get("fixedTransactionId"))
                .executeUpdate();
        transaction.commit();

        return new ConnectionResult<>(null);
    }
}

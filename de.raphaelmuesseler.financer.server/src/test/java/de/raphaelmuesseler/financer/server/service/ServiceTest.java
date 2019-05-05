package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.DatabaseName;
import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.shared.model.db.*;
import de.raphaelmuesseler.financer.shared.model.transactions.Attachment;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.Settings;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.shared.model.user.UserSettings;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class ServiceTest {
    private final FinancerService service = FinancerService.getInstance();
    private final Logger logger = Logger.getLogger("Test");

    private static UserDAO user;
    private static TokenDAO token;
    private static SettingsDAO settings;
    private static CategoryDAO fixedCategory, variableCategory;
    private static VariableTransactionDAO variableTransaction;
    private static FixedTransactionDAO fixedTransaction;

    @BeforeAll
    public static void beforeAll() {
        HibernateUtil.setIsHostLocal(false);
        HibernateUtil.setDatabaseName(DatabaseName.TEST);
    }

    @BeforeEach
    public void beforeEach() {
        // cleaning database
        HibernateUtil.cleanDatabase();

        // inserting mock data
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        user = new UserDAO();
        user.setEmail("max@mustermann.com");
        user.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        user.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");
        user.setName("Max");
        user.setSurname("Mustermann");
        user.setBirthDate(LocalDate.of(1989, 5, 28));
        user.setGenderName(User.Gender.MALE.getName());

        token = new TokenDAO();
        token.setToken("UrsVQcFmbje2lijl51mKMdAYCQciWoEmp07oLBrPoJwnEeREOBGVVsTAJeN3KiEY");
        token.setIpAddress("127.0.0.1");
        token.setSystem("Windows 10");
        token.setExpireDate(LocalDate.now().plusMonths(1));
        token.setIsMobile(false);
        token.setUser(user);

        user.setTokens(new HashSet<>());
        user.getTokens().add(token);

        settings = new SettingsDAO();
        settings.setProperty(Settings.Property.CURRENCY.getName());
        settings.setValue("EUR");
        settings.setUser(user);
        user.setDatabaseSettings(new HashSet<>());
        user.getDatabaseSettings().add(settings);

        session.save(user);

        transaction.commit();
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        transaction = session.beginTransaction();

        Set<CategoryDAO> categories = new HashSet<>();
        fixedCategory = new CategoryDAO();
        fixedCategory.setUser(user);
        fixedCategory.setCategoryRoot(0);
        fixedCategory.setName("First Layer");
        fixedCategory.setParentId(-1);
        categories.add(fixedCategory);
        session.save(fixedCategory);

        variableCategory = new CategoryDAO();
        variableCategory.setUser(user);
        variableCategory.setCategoryRoot(1);
        variableCategory.setName("First Layer");
        variableCategory.setParentId(-1);
        categories.add(variableCategory);
        session.save(variableCategory);

        CategoryDAO databaseCategory2 = new CategoryDAO();
        databaseCategory2.setUser(user);
        databaseCategory2.setCategoryRoot(0);
        databaseCategory2.setName("Second Layer");
        databaseCategory2.setParentId(fixedCategory.getId());
        session.save(databaseCategory2);
        categories.add(databaseCategory2);

        CategoryDAO databaseCategory3 = new CategoryDAO();
        databaseCategory3.setUser(user);
        databaseCategory3.setCategoryRoot(0);
        databaseCategory3.setName("Third Layer (1)");
        databaseCategory3.setParentId(databaseCategory2.getId());
        session.save(databaseCategory3);
        categories.add(databaseCategory3);

        CategoryDAO databaseCategory4 = new CategoryDAO();
        databaseCategory4.setUser(user);
        databaseCategory4.setCategoryRoot(0);
        databaseCategory4.setName("Third Layer (2)");
        databaseCategory4.setParentId(databaseCategory2.getId());
        session.save(databaseCategory4);
        categories.add(databaseCategory4);

        user.setCategories(categories);

        transaction.commit();
        session = HibernateUtil.getSessionFactory().getCurrentSession();
        transaction = session.beginTransaction();

        variableTransaction = new VariableTransactionDAO();
        variableTransaction.setAmount(50.0);
        variableTransaction.setCategory(variableCategory);
        variableTransaction.setProduct("Test Product");
        variableTransaction.setValueDate(LocalDate.now());
        session.save(variableTransaction);

        fixedTransaction = new FixedTransactionDAO();
        fixedTransaction.setAmount(50.0);
        fixedTransaction.setCategory(fixedCategory);
        fixedTransaction.setProduct("Fixed Transaction Product");
        fixedTransaction.setStartDate(LocalDate.now().minusMonths(5));
        fixedTransaction.setIsVariable(false);
        fixedTransaction.setDay(0);
        session.save(fixedTransaction);

        transaction.commit();
    }

    @Test
    public void testCheckUsersToken() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("token", token.getToken());
        final String tokenString = token.getToken();
        User userToAssert = service.checkUsersToken(logger, parameters);
        Assertions.assertNotNull(userToAssert);
        Assertions.assertEquals(1, userToAssert.getTokens().size());
        for (TokenDAO _token : userToAssert.getTokens()) {
            Assertions.assertEquals(tokenString, _token.getToken());
        }

        parameters.put("token", "testToken");
        userToAssert = service.checkUsersToken(logger, parameters);
        Assertions.assertNull(userToAssert);

    }

    @Test
    public void testGenerateToken() {
        User _user = new User(user);
        final String tokenString = token.getToken();

        // test updating token
        _user = service.generateToken(_user, token.getIpAddress(), token.getSystem(), token.getIsMobile());
        Assertions.assertEquals(1, _user.getTokens().size());
        Assertions.assertNotNull(_user.getActiveToken());
        for (TokenDAO _token : _user.getTokens()) {
            Assertions.assertNotEquals(tokenString, _token.getToken());
        }

        // test inserting new token
        _user = service.generateToken(_user, "123.456.789.0", token.getSystem(), token.getIsMobile());
        Assertions.assertEquals(2, _user.getTokens().size());
    }

    @Test
    public void testDeleteToken() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("tokenId", token.getId());
        service.deleteToken(logger, parameters);

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        User userToAssert = new User(session.get(UserDAO.class, user.getId()));

        Assertions.assertEquals(0, userToAssert.getTokens().size());

        transaction.commit();
    }

    @Test
    public void testCheckCredentials() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("password", "password");
        parameters.put("ipAddress", token.getIpAddress());
        parameters.put("system", token.getSystem());
        parameters.put("isMobile", token.getIsMobile());
        ConnectionResult<User> result = service.checkCredentials(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());

        parameters.put("password", "wrongPassword");
        result = service.checkCredentials(logger, parameters);
        Assertions.assertNull(result.getResult());

        parameters.put("email", "test@test.com");
        result = service.checkCredentials(logger, parameters);
        Assertions.assertNull(result.getResult());
    }

    @Test
    public void testRegisterUser() {
        HashMap<String, Object> parameters = new HashMap<>();
        User _user = new User(0,
                "other.email@test.com",
                "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
                "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
                "Test",
                "User",
                LocalDate.now(),
                User.Gender.NOT_SPECIFIED);
        parameters.put("user", _user);
        parameters.put("ipAddress", token.getIpAddress());
        parameters.put("system", token.getSystem());
        parameters.put("isMobile", token.getIsMobile());
        ConnectionResult<User> result = service.registerUser(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertTrue(result.getResult().getId() > 0);
        Assertions.assertEquals(1, result.getResult().getTokens().size());

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        User userToAssert = new User(session.get(UserDAO.class, result.getResult().getId()));

        Assertions.assertEquals(_user.getEmail(), userToAssert.getEmail());
        Assertions.assertEquals(_user.getFullName(), userToAssert.getFullName());

        transaction.commit();
    }

    @Test
    public void testChangePassword() {
        final String salt = new RandomString(32).nextString();
        final String password = Hash.create("newPassword", salt);
        user.setPassword(password);
        user.setSalt(salt);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", new User(user));
        service.changePassword(logger, parameters);

        parameters.clear();
        parameters.put("email", user.getEmail());
        parameters.put("password", "newPassword");
        parameters.put("ipAddress", token.getIpAddress());
        parameters.put("system", token.getSystem());
        parameters.put("isMobile", token.getIsMobile());
        ConnectionResult<User> result = service.checkCredentials(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
    }

    @Test
    public void testGetUsersSettings() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        User userToAssert = new User(session.get(UserDAO.class, user.getId()));

        Assertions.assertEquals(settings.getValue(), userToAssert.getSettings().getValueByProperty(Settings.Property.CURRENCY));
        transaction.commit();
    }

    @Test
    public void testUpdateUsersSettings() {
        SettingsDAO databaseSettings = new SettingsDAO();
        databaseSettings.setUser(user);
        databaseSettings.setProperty(Settings.Property.SHOW_CURRENCY_SIGN.getName());
        databaseSettings.setValue(Boolean.toString(true));
        user.getDatabaseSettings().add(databaseSettings);

        settings.setValue("USD");

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", new User(user));
        service.updateUsersSettings(logger, parameters);

        Assertions.assertEquals(2, user.getDatabaseSettings().size());
        Assertions.assertEquals(Currency.getInstance("USD"), ((UserSettings) new User(user).getSettings()).getCurrency());
        Assertions.assertTrue(((UserSettings) new User(user).getSettings()).isShowCurrencySign());
    }

    @Test
    public void testGetUsersCategories() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        ConnectionResult<BaseCategory> result = service.getUsersCategories(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertEquals(1, result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().size());

        Assertions.assertEquals("First Layer", result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getValue().getName());
        Assertions.assertEquals("Second Layer", result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getChildren().get(0).getValue().getName());
        Assertions.assertEquals(2, result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getChildren().get(0).getChildren().size());
    }

    @Test
    public void testAddCategory() {
        Category category = new Category();
        category.setUser(user);
        category.setCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE);
        category.setName("Another fixedCategory");
        category.setParentId(-1);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", category);
        service.addCategory(logger, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        Assertions.assertEquals(2, service.getUsersCategories(logger, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().size());
    }

    @Test
    public void testUpdateCategory() {
        fixedCategory.setName("New Name");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", new Category(fixedCategory));
        service.updateCategory(logger, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        Assertions.assertEquals("New Name", service.getUsersCategories(logger, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().get(0).getValue().getName());
    }

    @Test
    public void testDeleteCategory() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("categoryId", fixedCategory.getId());
        service.deleteCategory(logger, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        Assertions.assertEquals(0, service.getUsersCategories(logger, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().size());
    }

    @Test
    public void testGetTransactions() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        ConnectionResult<BaseCategory> result = service.getTransactions(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());

        Assertions.assertEquals(1, result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                .getChildren().get(0).getTransactions().size());
        for (de.raphaelmuesseler.financer.shared.model.transactions.Transaction transaction :
                result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                        .getChildren().get(0).getTransactions()) {
            if (transaction instanceof VariableTransaction) {
                Assertions.assertEquals(transaction.getId(), variableTransaction.getId());
            }
        }
    }

    @Test
    public void testAddTransaction() {
        VariableTransaction variableTransaction = new VariableTransaction(-1,
                25.0,
                LocalDate.now(),
                new CategoryTreeImpl(new Category(variableCategory)),
                "Another Procuct",
                "",
                "");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("variableTransaction", variableTransaction);
        ConnectionResult<VariableTransaction> result = service.addTransaction(logger, parameters);
        Assertions.assertTrue(result.getResult().getId() > 0);

        parameters.clear();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        Assertions.assertEquals(2, service.getTransactions(logger, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }

    @Test
    public void testUpdateTransaction() {
        final String newProduct = "A different product";
        variableTransaction.setProduct(newProduct);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("variableTransaction", new VariableTransaction(variableTransaction));
        service.updateTransaction(logger, parameters);

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        VariableTransaction variableTransactionToAssert = new VariableTransaction(session.get(VariableTransactionDAO.class,
                variableTransaction.getId()));
        Assertions.assertEquals(newProduct, variableTransactionToAssert.getProduct());
        transaction.commit();
    }

    @Test
    public void testDeleteTransaction() {
        this.testAddTransaction();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("variableTransactionId", variableTransaction.getId());
        service.deleteTransaction(logger, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        Assertions.assertEquals(1, service.getTransactions(logger, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }

    @Test
    public void testUploadAttachment() throws SQLException {
        RandomString randomString = new RandomString(1024);
        byte[] content = randomString.nextString().getBytes();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("attachmentFile", new File("test.txt"));
        parameters.put("transaction", new VariableTransaction(variableTransaction));
        parameters.put("content", content);
        ConnectionResult<Attachment> result = service.uploadTransactionAttachment(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertTrue(result.getResult().getId() > 0);
    }

    @Test
    public void testGetAttachment() throws SQLException {
        testUploadAttachment();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("attachmentId", 1);
        ConnectionResult<Attachment> result = service.getAttachment(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertTrue(result.getResult().getByteContent() != null && result.getResult().getByteContent().length == 1024);
    }

    @Test
    public void testDeleteAttachment() throws SQLException {
        testUploadAttachment();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("attachmentId", 1);
        service.deleteAttachment(logger, parameters);
        Assertions.assertNull(service.getAttachment(logger, parameters).getResult());
    }

    @Test
    public void testGetFixedTransactions() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        ConnectionResult<BaseCategory> result = service.getFixedTransactions(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());

        Assertions.assertEquals(1, result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getTransactions().size());
        for (de.raphaelmuesseler.financer.shared.model.transactions.Transaction transaction :
                result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                        .getChildren().get(0).getTransactions()) {
            if (transaction instanceof FixedTransaction) {
                Assertions.assertEquals(transaction.getId(), fixedTransaction.getId());
            }
        }
    }

    @Test
    public void testAddFixedTransaction() {
        Set<TransactionAmount> transactionAmounts = new HashSet<>();
        TransactionAmount transactionAmount = new TransactionAmount(0, 2.0, LocalDate.now().withDayOfMonth(1));
        transactionAmounts.add(transactionAmount);
        FixedTransaction _fixedTransaction = new FixedTransaction(0,
                50.0,
                new CategoryTreeImpl(new Category(fixedCategory)),
                LocalDate.now(),
                null,
                "Product",
                "Purpose",
                true,
                1,
                transactionAmounts);

        transactionAmount.setFixedTransaction(_fixedTransaction.toDatabaseAccessObject());

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fixedTransaction", _fixedTransaction);
        ConnectionResult<FixedTransaction> result = service.addFixedTransactions(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertTrue(result.getResult().getId() > 0);

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Assertions.assertEquals(LocalDate.now(), session.get(FixedTransactionDAO.class, fixedTransaction.getId()).getEndDate());
        transaction.commit();
    }

    @Test
    public void testUpdateFixedTransaction() {
        final double amount = 100.0;
        fixedTransaction.setAmount(amount);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fixedTransaction", new FixedTransaction(fixedTransaction));
        service.updateFixedTransaction(logger, parameters);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Assertions.assertEquals(amount, session.get(FixedTransactionDAO.class, fixedTransaction.getId()).getAmount());
        transaction.commit();
    }

    @Test
    public void testDeleteFixedTransaction() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fixedTransactionId", fixedTransaction.getId());
        service.deleteFixedTransaction(logger, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        ConnectionResult<BaseCategory> result = service.getFixedTransactions(logger, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());

        Assertions.assertEquals(0, result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }
}

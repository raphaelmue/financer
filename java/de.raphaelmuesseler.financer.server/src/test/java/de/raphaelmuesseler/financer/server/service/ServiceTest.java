package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.DatabaseName;
import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.shared.model.db.*;
import de.raphaelmuesseler.financer.shared.model.transactions.*;
import de.raphaelmuesseler.financer.shared.model.user.Settings;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
@Tag("unit")
public class ServiceTest {
    private final FinancerService service = FinancerService.getInstance();
    private final Logger logger = Logger.getLogger("Test");

    private static UserEntity user;
    private static TokenEntity token;
    private static SettingsEntity settings;
    private static CategoryEntity fixedCategory, variableCategory;
    private static VariableTransactionEntity variableTransaction;
    private static FixedTransactionEntity fixedTransaction;

    private static Session session;

    @BeforeAll
    public static void beforeAll() throws IOException {
        InputStream inputStream = ServiceTest.class.getResourceAsStream("/testing.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        HibernateUtil.setIsHostLocal(Boolean.valueOf(properties.getProperty("project.testing.localhost")));
        HibernateUtil.setDatabaseName(DatabaseName.TEST);
    }

    @BeforeEach
    public void beforeEach() {
        // cleaning database
        HibernateUtil.cleanDatabase();

        // inserting mock data
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        user = new UserEntity();
        user.setEmail("max@mustermann.com");
        user.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        user.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");
        user.setName("Max");
        user.setSurname("Mustermann");
        user.setBirthDate(LocalDate.of(1989, 5, 28));
        user.setGenderName(User.Gender.MALE.getName());

        user.setId((int) session.save(user));
        transaction.commit();
        transaction = session.beginTransaction();

        token = new TokenEntity();
        token.setToken("UrsVQcFmbje2lijl51mKMdAYCQciWoEmp07oLBrPoJwnEeREOBGVVsTAJeN3KiEY");
        token.setIpAddress("127.0.0.1");
        token.setSystem("Windows 10");
        token.setExpireDate(LocalDate.now().plusMonths(1));
        token.setIsMobile(false);
        token.setUser(user);

        settings = new SettingsEntity();
        settings.setProperty(Settings.Property.CURRENCY.getName());
        settings.setValue("EUR");
        settings.setUser(user);

        session.save(token);
        session.save(settings);

        transaction.commit();

        user.setDatabaseSettings(new HashSet<>());
        user.getDatabaseSettings().add(settings);

        user.setTokens(new HashSet<>());
        user.getTokens().add(token);

        transaction = session.beginTransaction();

        Set<CategoryEntity> categories = new HashSet<>();
        fixedCategory = new CategoryEntity();
        fixedCategory.setUser(user);
        fixedCategory.setCategoryRoot(0);
        fixedCategory.setName("First Layer");
        fixedCategory.setParentId(-1);
        categories.add(fixedCategory);
        fixedCategory.setId((int) session.save(fixedCategory));

        variableCategory = new CategoryEntity();
        variableCategory.setUser(user);
        variableCategory.setCategoryRoot(1);
        variableCategory.setName("First Layer");
        variableCategory.setParentId(-1);
        categories.add(variableCategory);
        variableCategory.setId((int) session.save(variableCategory));

        CategoryEntity databaseCategory2 = new CategoryEntity();
        databaseCategory2.setUser(user);
        databaseCategory2.setCategoryRoot(0);
        databaseCategory2.setName("Second Layer");
        databaseCategory2.setParentId(fixedCategory.getId());
        databaseCategory2.setId((int) session.save(databaseCategory2));
        categories.add(databaseCategory2);

        CategoryEntity databaseCategory3 = new CategoryEntity();
        databaseCategory3.setUser(user);
        databaseCategory3.setCategoryRoot(0);
        databaseCategory3.setName("Third Layer (1)");
        databaseCategory3.setParentId(databaseCategory2.getId());
        session.save(databaseCategory3);
        categories.add(databaseCategory3);

        CategoryEntity databaseCategory4 = new CategoryEntity();
        databaseCategory4.setUser(user);
        databaseCategory4.setCategoryRoot(0);
        databaseCategory4.setName("Third Layer (2)");
        databaseCategory4.setParentId(databaseCategory2.getId());
        session.save(databaseCategory4);
        categories.add(databaseCategory4);

        user.setCategories(categories);

        transaction.commit();
        transaction = session.beginTransaction();

        variableTransaction = new VariableTransactionEntity();
        variableTransaction.setAmount(50.0);
        variableTransaction.setCategory(variableCategory);
        variableTransaction.setProduct("Test Product");
        variableTransaction.setValueDate(LocalDate.now());
        session.save(variableTransaction);

        fixedTransaction = new FixedTransactionEntity();
        fixedTransaction.setAmount(50.0);
        fixedTransaction.setCategory(fixedCategory);
        fixedTransaction.setProduct("Fixed Transaction Product");
        fixedTransaction.setStartDate(LocalDate.now().minusMonths(5));
        fixedTransaction.setIsVariable(false);
        fixedTransaction.setDay(0);
        fixedTransaction.setTransactionAmounts(new HashSet<>());
        session.save(fixedTransaction);

        transaction.commit();
        session.close();
        ServiceTest.session = HibernateUtil.getSessionFactory().openSession();
    }

    @AfterEach
    public void tearDown() {
        session.close();
    }

    @Test
    public void testCheckUsersToken() {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("token", token.getToken());
        final String tokenString = token.getToken();
        User userToAssert = service.checkUsersToken(logger, session, parameters);
        Assertions.assertNotNull(userToAssert);
        Assertions.assertEquals(1, userToAssert.getTokens().size());
        for (TokenEntity _token : userToAssert.getTokens()) {
            Assertions.assertEquals(tokenString, _token.getToken());
        }

        parameters.put("token", "testToken");
        userToAssert = service.checkUsersToken(logger, session, parameters);
        Assertions.assertNull(userToAssert);

    }

    @Test
    public void testGenerateToken() {
        User _user = new User(user);
        final String tokenString = token.getToken();

        // test updating token
        _user = service.generateToken(session, _user, token.getIpAddress(), token.getSystem(), token.getIsMobile());
        Assertions.assertEquals(1, _user.getTokens().size());
        Assertions.assertNotNull(_user.getActiveToken());
        for (TokenEntity _token : _user.getTokens()) {
            Assertions.assertNotEquals(tokenString, _token.getToken());
        }

        // test inserting new token
        _user = service.generateToken(session, _user, "123.456.789.0", token.getSystem(), token.getIsMobile());
        Assertions.assertEquals(2, _user.getTokens().size());
    }

    @Test
    public void testDeleteToken() {
        HashMap<String, Serializable> parameters = new HashMap<>();
        parameters.put("tokenId", token.getId());
        service.deleteToken(logger, session, parameters);

        Transaction transaction = session.beginTransaction();
        User userToAssert = new User(session.get(UserEntity.class, user.getId()));

        Assertions.assertEquals(0, userToAssert.getTokens().size());

        transaction.commit();
    }

    @Test
    public void testCheckCredentials() {
        HashMap<String, Serializable> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("password", "password");
        parameters.put("ipAddress", token.getIpAddress());
        parameters.put("system", token.getSystem());
        parameters.put("isMobile", token.getIsMobile());
        ConnectionResult<User> result = service.checkCredentials(logger, session, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());

        parameters.put("password", "wrongPassword");
        result = service.checkCredentials(logger, session, parameters);
        Assertions.assertNull(result.getResult());

        parameters.put("email", "test@test.com");
        result = service.checkCredentials(logger, session, parameters);
        Assertions.assertNull(result.getResult());
    }

    @Test
    public void testRegisterUser() {
        HashMap<String, Serializable> parameters = new HashMap<>();
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
        ConnectionResult<User> result = service.registerUser(logger, session, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertTrue(result.getResult().getId() > 0);
        Assertions.assertEquals(1, result.getResult().getTokens().size());

        Transaction transaction = session.beginTransaction();
        User userToAssert = new User(session.get(UserEntity.class, result.getResult().getId()));

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

        HashMap<String, Serializable> parameters = new HashMap<>();
        parameters.put("user", new User(user));
        service.changePassword(logger, session, parameters);

        parameters.clear();
        parameters.put("email", user.getEmail());
        parameters.put("password", "newPassword");
        parameters.put("ipAddress", token.getIpAddress());
        parameters.put("system", token.getSystem());
        parameters.put("isMobile", token.getIsMobile());
        ConnectionResult<User> result = service.checkCredentials(logger, session, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
    }

    @Test
    public void testGetUsersSettings() {
        Transaction transaction = session.beginTransaction();
        User userToAssert = new User(session.get(UserEntity.class, user.getId()));

        Assertions.assertEquals(settings.getValue(), userToAssert.getSettings().getValueByProperty(Settings.Property.CURRENCY));
        transaction.commit();
    }

    @Test
    public void testUpdateUsersSettings() {
        SettingsEntity databaseSettings = new SettingsEntity();
        databaseSettings.setUser(user);
        databaseSettings.setProperty(Settings.Property.SHOW_CURRENCY_SIGN.getName());
        databaseSettings.setValue(Boolean.toString(true));
        user.getDatabaseSettings().add(databaseSettings);

        settings.setValue("USD");

        HashMap<String, Serializable> parameters = new HashMap<>();
        parameters.put("user", new User(user));
        service.updateUsersSettings(logger, session, parameters);

        Assertions.assertEquals(2, user.getDatabaseSettings().size());
        Assertions.assertEquals(Currency.getInstance("USD"), new User(user).getSettings().getCurrency());
        Assertions.assertTrue(new User(user).getSettings().isShowCurrencySign());
    }

    @Test
    public void testGetUsersCategories() {
        HashMap<String, Serializable> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        ConnectionResult<BaseCategory> result = service.getUsersCategories(logger, session, parameters);
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

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("category", category);
        service.addCategory(logger, session, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        Assertions.assertEquals(2, service.getUsersCategories(logger, session, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().size());
    }

    @Test
    public void testUpdateCategory() {
        fixedCategory.setName("New Name");

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("category", new Category(fixedCategory));
        service.updateCategory(logger, session, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        Assertions.assertEquals("New Name", service.getUsersCategories(logger, session, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().get(0).getValue().getName());
    }

    @Test
    public void testDeleteCategory() {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("categoryId", fixedCategory.getId());
        service.deleteCategory(logger, session, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        Assertions.assertEquals(0, service.getUsersCategories(logger, session, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().size());
    }

    @Test
    public void testGetTransactions() {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, session, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        ConnectionResult<BaseCategory> result = service.getTransactions(logger, session, parameters);
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
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("variableTransaction", variableTransaction);
        ConnectionResult<VariableTransaction> result = service.addTransaction(logger, session, parameters);
        Assertions.assertTrue(result.getResult().getId() > 0);

        parameters.clear();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, session, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        Assertions.assertEquals(2, service.getTransactions(logger, session, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }

    @Test
    public void testUpdateTransaction() {
        final String newProduct = "A different product";
        variableTransaction.setProduct(newProduct);

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("variableTransaction", new VariableTransaction(variableTransaction));
        service.updateTransaction(logger, session, parameters);

        Transaction transaction = session.beginTransaction();
        VariableTransaction variableTransactionToAssert = new VariableTransaction(session.get(VariableTransactionEntity.class,
                variableTransaction.getId()));
        Assertions.assertEquals(newProduct, variableTransactionToAssert.getProduct());
        transaction.commit();
    }

    @Test
    public void testDeleteTransaction() {
        this.testAddTransaction();

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("variableTransactionId", variableTransaction.getId());
        service.deleteTransaction(logger, session, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, session, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        Assertions.assertEquals(1, service.getTransactions(logger, session, parameters).getResult()
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }

    @Test
    public void testUploadAttachment() {
        RandomString randomString = new RandomString(1024);
        ContentAttachment content = new ContentAttachment();
        content.setTransaction(variableTransaction);
        content.setName("Test Attachment");
        content.setContent(randomString.nextString().getBytes());

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("attachment", content);
        parameters.put("transaction", new VariableTransaction(variableTransaction));
        ConnectionResult<Attachment> result = service.uploadTransactionAttachment(logger, session, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertTrue(result.getResult().getId() > 0);
    }

    @Test
    public void testGetAttachment() {
        testUploadAttachment();

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("attachmentId", 1);
        ConnectionResult<ContentAttachment> result = service.getAttachment(logger, session, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertTrue(result.getResult().getContent() != null && result.getResult().getContent().length == 1024);
    }

    @Test
    public void testDeleteAttachment() {
        testUploadAttachment();

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("attachmentId", 1);
        service.deleteAttachment(logger, session, parameters);
        session.close();
        session = HibernateUtil.getSessionFactory().openSession();
        Assertions.assertNull(service.getAttachment(logger, session, parameters).getResult());
    }

    @Test
    public void testGetFixedTransactions() {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, session, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        ConnectionResult<BaseCategory> result = service.getFixedTransactions(logger, session, parameters);
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

        transactionAmount.setFixedTransaction(_fixedTransaction.toEntity());

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("fixedTransaction", _fixedTransaction);
        ConnectionResult<FixedTransaction> result = service.addFixedTransactions(logger, session, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());
        Assertions.assertTrue(result.getResult().getId() > 0);

        Transaction transaction = session.beginTransaction();
        Assertions.assertEquals(LocalDate.now(), session.get(FixedTransactionEntity.class, fixedTransaction.getId()).getEndDate());
        transaction.commit();
    }

    @Test
    public void testUpdateFixedTransaction() {
        final double amount = 100.0;
        fixedTransaction.setAmount(amount);

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("fixedTransaction", new FixedTransaction(fixedTransaction));
        service.updateFixedTransaction(logger, session, parameters);

        Transaction transaction = session.beginTransaction();
        Assertions.assertEquals(amount, session.get(FixedTransactionEntity.class, fixedTransaction.getId()).getAmount());
        transaction.commit();
    }

    @Test
    public void testDeleteFixedTransaction() {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("fixedTransactionId", fixedTransaction.getId());
        service.deleteFixedTransaction(logger, session, parameters);

        parameters.clear();
        parameters.put("userId", user.getId());
        BaseCategory baseCategory = service.getUsersCategories(logger, session, parameters).getResult();

        parameters.clear();
        parameters.put("userId", user.getId());
        parameters.put("baseCategory", baseCategory);
        ConnectionResult<BaseCategory> result = service.getFixedTransactions(logger, session, parameters);
        Assertions.assertNotNull(result.getResult());
        Assertions.assertNull(result.getException());

        Assertions.assertEquals(0, result.getResult().getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }

    @Test
    public void testAddTransactionAmount() {
        Map<String, Serializable> parameters = new HashMap<>();
        TransactionAmount transactionAmount = new TransactionAmount(0, 5.5, LocalDate.now());
        transactionAmount.setFixedTransaction(fixedTransaction);
        parameters.put("transactionAmount", transactionAmount);

        ConnectionResult<TransactionAmount> result = service.addTransactionAmount(logger, session, parameters);
        Assertions.assertTrue(result.getResult().getId() > 0);
    }
}

package org.financer.server.service;

import org.financer.server.configuration.PersistenceConfiguration;
import org.financer.server.configuration.ServiceConfiguration;
import org.financer.shared.model.categories.BaseCategory;
import org.financer.shared.model.categories.Category;
import org.financer.shared.model.categories.CategoryTreeImpl;
import org.financer.shared.model.db.*;
import org.financer.shared.model.transactions.*;
import org.financer.shared.model.user.Settings;
import org.financer.shared.model.user.User;
import org.financer.util.Hash;
import org.financer.util.RandomString;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Tag("unit")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceConfiguration.class, PersistenceConfiguration.class})
public class FinancerServiceTest {

    @Autowired
    private FinancerService service;

    @Autowired
    private SessionFactory sessionFactory;

    private static UserEntity user;
    private static TokenEntity token;
    private static SettingsEntity settings;
    private static CategoryEntity fixedCategory, variableCategory;
    private static VariableTransactionEntity variableTransaction;
    private static FixedTransactionEntity fixedTransaction;
    private static ContentAttachment contentAttachment;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        Session session = sessionFactory.getCurrentSession();

        user = new UserEntity();
        user.setEmail("info@financer-project.org");
        user.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        user.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");
        user.setName("Max");
        user.setSurname("Mustermann");
        user.setBirthDate(LocalDate.of(1989, 5, 28));
        user.setGenderName(User.Gender.MALE.getName());

        user.setId((int) session.save(user));

        token = new TokenEntity();
        token.setToken("UrsVQcFmbje2lijl51mKMdAYCQciWoEmp07oLBrPoJwnEeREOBGVVsTAJeN3KiEY");
        token.setIpAddress("127.0.0.1");
        token.setOperatingSystem("Windows 10");
        token.setExpireDate(LocalDate.now().plusMonths(1));
        token.setIsMobile(false);
        token.setUser(user);

        VerificationTokenEntity verificationToken = new VerificationTokenEntity();
        verificationToken.setUser(user);
        verificationToken.setToken("eCqPPZGRj2bzLveAudsbyjUk8K8jAigXKKYnvjrnx24Lg8pXYHncD3yej8ic6yeK2x2rGonCFR4aDX6kjcSNoZRfvAmVyZN5bpQFfDviXqnA7ZZK6fr7CiQywk93uMvm");
        verificationToken.setExpireDate(LocalDate.now().plusMonths(1));

        settings = new SettingsEntity();
        settings.setProperty(Settings.Property.CURRENCY.getName());
        settings.setValue("EUR");
        settings.setUser(user);

        session.save(token);
        session.save(verificationToken);
        session.save(settings);

        user.setDatabaseSettings(new HashSet<>());
        user.getDatabaseSettings().add(settings);

        user.setTokens(new HashSet<>());
        user.getTokens().add(token);

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
    }

    @Test
    @Transactional
    public void testCheckUsersToken() {
        User userToAssert = service.checkUsersToken(token.getToken());
        Assertions.assertNotNull(userToAssert);
        Assertions.assertEquals(1, userToAssert.getTokens().size());
        for (TokenEntity _token : userToAssert.getTokens()) {
            Assertions.assertEquals(token.getToken(), _token.getToken());
        }

        userToAssert = service.checkUsersToken("testToken");
        Assertions.assertNull(userToAssert);

    }

    @Test
    @Transactional
    public void testGenerateToken() {
        User _user = new User(user);
        final String tokenString = token.getToken();

        // test updating token
        _user = service.generateToken(_user, token.getIpAddress(), token.getOperatingSystem(), token.getIsMobile());
        Assertions.assertEquals(1, _user.getTokens().size());
        Assertions.assertNotNull(_user.getActiveToken());
        for (TokenEntity _token : _user.getTokens()) {
            Assertions.assertNotEquals(tokenString, _token.getToken());
        }

        // test inserting new token
        _user = service.generateToken(_user, "123.456.789.0", token.getOperatingSystem(), token.getIsMobile());
        Assertions.assertEquals(2, _user.getTokens().size());
    }

    @Test
    @Transactional
    public void testDeleteToken() {
        Session session = sessionFactory.getCurrentSession();
        service.deleteToken(token.getId());
        User userToAssert = new User(session.get(UserEntity.class, user.getId()));
        Assertions.assertEquals(0, userToAssert.getTokens().size());
    }

    @Test
    @Transactional
    public void testCheckCredentials() {
        User result = service.checkCredentials(user.getEmail(),
                "password",
                token.getIpAddress(),
                token.getOperatingSystem(),
                token.getIsMobile());
        Assertions.assertNotNull(result);

        result = service.checkCredentials(user.getEmail(),
                "wrongPassword",
                token.getIpAddress(),
                token.getOperatingSystem(),
                token.getIsMobile());
        Assertions.assertNull(result);

        result = service.checkCredentials("test@test.com",
                "password",
                token.getIpAddress(),
                token.getOperatingSystem(),
                token.getIsMobile());
        Assertions.assertNull(result);
    }

    @Test
    @Transactional
    public void testRegisterUser() {
        Session session = sessionFactory.getCurrentSession();
        User _user = new User(0,
                "other.email@test.com",
                "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
                "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
                "Test",
                "User",
                LocalDate.now(),
                User.Gender.NOT_SPECIFIED,
                false);

        User result = service.registerUser(_user, token.getIpAddress(), token.getOperatingSystem(), token.getIsMobile());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertEquals(1, result.getTokens().size());

        User userToAssert = new User(session.get(UserEntity.class, result.getId()));

        Assertions.assertEquals(_user.getEmail(), userToAssert.getEmail());
        Assertions.assertEquals(_user.getFullName(), userToAssert.getFullName());
    }

    @Test
    @Transactional
    public void testVerifyUser() {
        Session session = sessionFactory.getCurrentSession();
        Assertions.assertFalse(user.getVerified());

        User result = service.verifyUser(user.getId(), "verification token");
        Assertions.assertFalse(result.getVerified());

        result = service.verifyUser(user.getId(),
                "eCqPPZGRj2bzLveAudsbyjUk8K8jAigXKKYnvjrnx24Lg8pXYHncD3yej8ic6yeK2x2rGonCFR4aDX6kjcSNoZR" +
                        "fvAmVyZN5bpQFfDviXqnA7ZZK6fr7CiQywk93uMvm");
        Assertions.assertTrue(result.getVerified());

        User userToAssert = new User(session.get(UserEntity.class, result.getId()));
        Assertions.assertTrue(userToAssert.getVerified());
    }

    @Test
    @Transactional
    public void testChangePersonalInformation() {
        final String salt = new RandomString(32).nextString();
        final String password = Hash.create("newPassword", salt);
        user.setPassword(password);
        user.setSalt(salt);

        service.updateUser(new User(user));
        User result = service.checkCredentials(user.getEmail(), user.getPassword(), token.getIpAddress(),
                token.getOperatingSystem(), token.getIsMobile());
        Assertions.assertNotNull(result);
    }

    @Test
    @Transactional
    public void testGetUsersSettings() {
        Session session = sessionFactory.getCurrentSession();
        User userToAssert = new User(session.get(UserEntity.class, user.getId()));
        Assertions.assertEquals(settings.getValue(), userToAssert.getSettings().getValueByProperty(Settings.Property.CURRENCY));
    }

    @Test
    @Transactional
    public void testUpdateUsersSettings() {
        SettingsEntity databaseSettings = new SettingsEntity();
        databaseSettings.setUser(user);
        databaseSettings.setProperty(Settings.Property.SHOW_CURRENCY_SIGN.getName());
        databaseSettings.setValue(Boolean.toString(true));
        user.getDatabaseSettings().add(databaseSettings);

        settings.setValue("USD");
        service.updateUsersSettings(new User(user));

        Assertions.assertEquals(2, user.getDatabaseSettings().size());
        Assertions.assertEquals(Currency.getInstance("USD"), new User(user).getSettings().getCurrency());
        Assertions.assertTrue(new User(user).getSettings().isShowCurrencySign());
    }

    @Test
    @Transactional
    public void testGetUsersCategories() {
        BaseCategory result = service.getUsersCategories(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().size());

        Assertions.assertEquals("First Layer", result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getValue().getName());
        Assertions.assertEquals("Second Layer", result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getChildren().get(0).getValue().getName());
        Assertions.assertEquals(2, result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getChildren().get(0).getChildren().size());
    }

    @Test
    @Transactional
    public void testAddCategory() {
        Category category = new Category();
        category.setUser(user);
        category.setCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE);
        category.setName("Another fixedCategory");
        category.setParentId(-1);

        service.addCategory(category);

        Assertions.assertEquals(2, service.getUsersCategories(user.getId())
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().size());
    }

    @Test
    @Transactional
    public void testUpdateCategory() {
        fixedCategory.setName("New Name");
        service.updateCategory(new Category(fixedCategory));
        Assertions.assertEquals("New Name", service.getUsersCategories(user.getId())
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().get(0).getValue().getName());
    }

    @Test
    @Transactional
    public void testDeleteCategory() {
        service.deleteCategory(fixedCategory.getId());
        Assertions.assertEquals(0, service.getUsersCategories(user.getId())
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE).getChildren().size());
    }

    @Test
    @Transactional
    public void testGetTransactions() {
        BaseCategory baseCategory = service.getUsersCategories(user.getId());

        BaseCategory result = service.getTransactions(user.getId(), baseCategory);
        Assertions.assertNotNull(result);

        Assertions.assertEquals(1, result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                .getChildren().get(0).getTransactions().size());
        for (org.financer.shared.model.transactions.Transaction transaction :
                result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                        .getChildren().get(0).getTransactions()) {
            if (transaction instanceof VariableTransaction) {
                Assertions.assertEquals(transaction.getId(), variableTransaction.getId());
            }
        }
    }

    @Test
    @Transactional
    public void testAddTransaction() {
        VariableTransaction variableTransaction = new VariableTransaction(-1,
                25.0,
                LocalDate.now(),
                new CategoryTreeImpl(new Category(variableCategory)),
                "Another Procuct",
                "",
                "");
        VariableTransaction result = service.addTransaction(variableTransaction);
        Assertions.assertTrue(result.getId() > 0);

        BaseCategory baseCategory = service.getUsersCategories(user.getId());
        Assertions.assertEquals(2, service.getTransactions(user.getId(), baseCategory)
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }

    @Test
    @Transactional
    public void testUpdateTransaction() {
        Session session = sessionFactory.getCurrentSession();
        final String newProduct = "A different product";
        variableTransaction.setProduct(newProduct);
        service.updateTransaction(new VariableTransaction(variableTransaction));

        VariableTransaction variableTransactionToAssert = new VariableTransaction(session.get(VariableTransactionEntity.class,
                variableTransaction.getId()));
        Assertions.assertEquals(newProduct, variableTransactionToAssert.getProduct());
    }

    @Test
    @Transactional
    public void testDeleteTransaction() {
        this.testAddTransaction();
        service.deleteTransaction(variableTransaction.getId());

        BaseCategory baseCategory = service.getUsersCategories(user.getId());
        Assertions.assertEquals(1, service.getTransactions(user.getId(), baseCategory)
                .getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.VARIABLE_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }

    @Test
    @Transactional
    public void testUploadAttachment() {
        RandomString randomString = new RandomString(1024);
        contentAttachment = new ContentAttachment();
        contentAttachment.setTransaction(variableTransaction);
        contentAttachment.setName("Test Attachment");
        contentAttachment.setContent(randomString.nextString().getBytes());

        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("attachment", contentAttachment);
        parameters.put("transaction", new VariableTransaction(variableTransaction));
        Attachment result = service.uploadTransactionAttachment(new VariableTransaction(variableTransaction), contentAttachment);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getId() > 0);
    }

    @Test
    @Transactional
    public void testGetAttachment() {
        testUploadAttachment();

        ContentAttachment result = service.getAttachment(1);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent() != null && result.getContent().length == 1024);
    }

    @Test
    @Transactional
    public void testDeleteAttachment() {
        testUploadAttachment();
        service.deleteAttachment(contentAttachment.getId());
        Assertions.assertNull(service.getAttachment(contentAttachment.getId()));
    }

    @Test
    @Transactional
    public void testGetFixedTransactions() {
        BaseCategory baseCategory = service.getUsersCategories(user.getId());
        BaseCategory result = service.getFixedTransactions(baseCategory);
        Assertions.assertNotNull(result);

        Assertions.assertEquals(1, result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getTransactions().size());
        for (org.financer.shared.model.transactions.Transaction transaction :
                result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                        .getChildren().get(0).getTransactions()) {
            if (transaction instanceof FixedTransaction) {
                Assertions.assertEquals(transaction.getId(), fixedTransaction.getId());
            }
        }
    }

    @Test
    @Transactional
    public void testAddFixedTransaction() {
        Session session = sessionFactory.getCurrentSession();
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

        FixedTransaction result = service.addFixedTransactions(_fixedTransaction);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getId() > 0);
        Assertions.assertEquals(LocalDate.now().minusDays(1),
                session.get(FixedTransactionEntity.class, fixedTransaction.getId()).getEndDate());
    }

    @Test
    @Transactional
    public void testUpdateFixedTransaction() {
        Session session = sessionFactory.getCurrentSession();
        final double amount = 100.0;
        FixedTransaction _fixedTransaction = new FixedTransaction(fixedTransaction);
        _fixedTransaction.setAmount(amount);
        TransactionAmount transactionAmount = new TransactionAmount(0, 500, LocalDate.now());
        transactionAmount.setFixedTransaction(fixedTransaction);
        _fixedTransaction.getTransactionAmounts().add(transactionAmount);

        _fixedTransaction = service.updateFixedTransaction(_fixedTransaction);

        Transaction transaction = session.beginTransaction();
        Assertions.assertEquals(amount, session.get(FixedTransactionEntity.class, fixedTransaction.getId()).getAmount());
        Assertions.assertEquals(1, session.get(FixedTransactionEntity.class, fixedTransaction.getId()).getTransactionAmounts().size());
        transaction.commit();
        session.close();

        _fixedTransaction.getTransactionAmounts().clear();
        service.updateFixedTransaction(_fixedTransaction);
        Assertions.assertEquals(0, session.get(FixedTransactionEntity.class, fixedTransaction.getId()).getTransactionAmounts().size());
    }

    @Test
    @Transactional
    public void testDeleteFixedTransaction() {
        service.deleteFixedTransaction(fixedTransaction.getId());
        BaseCategory baseCategory = service.getUsersCategories(user.getId());
        BaseCategory result = service.getFixedTransactions(baseCategory);
        Assertions.assertNotNull(result);

        Assertions.assertEquals(0, result.getCategoryTreeByCategoryClass(BaseCategory.CategoryClass.FIXED_REVENUE)
                .getChildren().get(0).getTransactions().size());
    }

    @Test
    @Transactional
    public void testAddTransactionAmount() {
        TransactionAmount transactionAmount = new TransactionAmount(0, 5.5, LocalDate.now());
        transactionAmount.setFixedTransaction(fixedTransaction);

        TransactionAmount result = service.addTransactionAmount(transactionAmount);
        Assertions.assertTrue(result.getId() > 0);
    }
}

package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.DatabaseName;
import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseSettings;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseToken;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
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

import java.time.LocalDate;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class ServiceTest {
    private final FinancerService service = FinancerService.getInstance();
    private final Logger logger = Logger.getLogger("Test");

    private static DatabaseUser user;
    private static DatabaseToken token;
    private static DatabaseSettings settings;

    @BeforeAll
    public static void beforeAll() {
        HibernateUtil.setIsHostLocal(true);
        HibernateUtil.setDatabaseName(DatabaseName.TEST);
    }

    @BeforeEach
    public void beforeEach() {
        // cleaning database
        HibernateUtil.cleanDatabase();

        // inserting mock data
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        user = new DatabaseUser();
        user.setEmail("max@mustermann.com");
        user.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        user.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");
        user.setName("Max");
        user.setSurname("Mustermann");
        user.setBirthDate(LocalDate.of(1989, 5, 28));
        user.setGenderName(User.Gender.MALE.getName());

        token = new DatabaseToken();
        token.setToken("UrsVQcFmbje2lijl51mKMdAYCQciWoEmp07oLBrPoJwnEeREOBGVVsTAJeN3KiEY");
        token.setIpAddress("127.0.0.1");
        token.setSystem("Windows 10");
        token.setExpireDate(LocalDate.now().plusMonths(1));
        token.setIsMobile(false);
        token.setUser(user);

        user.setTokens(new HashSet<>());
        user.getTokens().add(token);

        settings = new DatabaseSettings();
        settings.setProperty(Settings.Property.CURRENCY.getName());
        settings.setValue("EUR");
        settings.setUser(user);

        user.setDatabaseSettings(new HashSet<>());
        user.getDatabaseSettings().add(settings);

        session.save(user);
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
        for (DatabaseToken _token : userToAssert.getTokens()) {
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
        for (DatabaseToken _token : _user.getTokens()) {
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
        User userToAssert = new User(session.get(DatabaseUser.class, user.getId()));

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
        User userToAssert = new User(session.get(DatabaseUser.class, result.getResult().getId()));

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
        User userToAssert = new User(session.get(DatabaseUser.class, user.getId()));

        Assertions.assertEquals(settings.getValue(), userToAssert.getSettings().getValueByProperty(Settings.Property.CURRENCY));
        transaction.commit();
    }

    @Test
    public void testUpdateUsersSettings() {
        DatabaseSettings databaseSettings = new DatabaseSettings();
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
}

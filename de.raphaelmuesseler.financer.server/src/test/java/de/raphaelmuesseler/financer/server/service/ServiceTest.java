package de.raphaelmuesseler.financer.server.service;

import de.raphaelmuesseler.financer.server.db.DatabaseName;
import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseToken;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
import de.raphaelmuesseler.financer.shared.model.user.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class ServiceTest {
    private final FinancerService service = FinancerService.getInstance();
    private final Logger logger = Logger.getLogger("Test");

    private static DatabaseUser user;
    private static DatabaseToken token;

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
}

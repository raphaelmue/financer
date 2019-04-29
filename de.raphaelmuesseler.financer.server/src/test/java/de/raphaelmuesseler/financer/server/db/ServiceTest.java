package de.raphaelmuesseler.financer.server.db;

import de.raphaelmuesseler.financer.server.service.FinancerService;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseToken;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
import de.raphaelmuesseler.financer.shared.model.user.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class ServiceTest {
    private final FinancerService service = FinancerService.getInstance();
    private final Logger logger = Logger.getLogger("Test");

    private static final DatabaseUser user = new DatabaseUser();
    private static final DatabaseToken token = new DatabaseToken();

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

        user.setEmail("max@mustermann.com");
        user.setPassword("6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af");
        user.setSalt("I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5");
        user.setName("Max");
        user.setSurname("Mustermann");
        user.setBirthDate(LocalDate.of(1989, 5, 28));
        user.setGenderName(User.Gender.MALE.getName());

        token.setToken("UrsVQcFmbje2lijl51mKMdAYCQciWoEmp07oLBrPoJwnEeREOBGVVsTAJeN3KiEY");
        token.setIpAddress("127.0.0.1");
        token.setSystem("Windows 10");
        token.setExpireDate(LocalDate.now().plusMonths(1));
        token.setIsMobile(false);
        token.setUser(user);

        user.setTokens(new HashSet<>());
        user.getTokens().add(token);

        session.save(user);
        session.save(token);
        transaction.commit();
    }

    @Test
    public void testCheckUsersToken() throws SQLException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("token", token.getToken());
        User userToAssert = service.checkUsersToken(logger, parameters);

        Assertions.assertNotNull(userToAssert);
        Assertions.assertEquals(user.getTokens(), userToAssert.getTokens());


    }
}

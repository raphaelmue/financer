package de.raphaelmuesseler.financer.server.db;

import de.raphaelmuesseler.financer.server.service.FinancerService;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseToken;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseUser;
import de.raphaelmuesseler.financer.shared.model.user.Token;
import de.raphaelmuesseler.financer.shared.model.user.User;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class ServiceTest extends Mockito {
    private final FinancerService service = FinancerService.getInstance();
    private final Logger logger = Logger.getLogger("Test");

    private static final User user = new User(
            "max@mustermann.com",
            "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
            "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
            "Max",
            "Mustermann",
            LocalDate.of(1989, 5, 28),
            User.Gender.MALE);
    private static final Token token = new Token(
            1,
            "UrsVQcFmbje2lijl51mKMdAYCQciWoEmp07oLBrPoJwnEeREOBGVVsTAJeN3KiEY",
            "127.0.0.1",
            "Windows 10",
            LocalDate.now().plusMonths(1),
            false);

    @BeforeAll
    public static void setup() {
        HibernateUtil.setIsHostLocal(true);
        HibernateUtil.setDatabaseName(DatabaseName.TEST);

        user.getTokens().add(token);

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save((DatabaseUser) user);
        session.save((DatabaseToken) token);
    }

    @Test
    public void testCheckUsersToken() throws SQLException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("email", "raphael@muesseler.de");
        service.checkUsersToken(logger, parameters);
    }
}

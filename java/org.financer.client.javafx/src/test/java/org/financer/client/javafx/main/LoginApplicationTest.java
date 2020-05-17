package org.financer.client.javafx.main;

import org.financer.client.domain.model.user.User;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class LoginApplicationTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testInvalidLogin() {
        login(user(), "wrong");
        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#loginErrorLabel").isVisible());
    }

    @Test
    public void testRegisterUser() {
        final User user = user();
        register(user, password());
        User storedUser = LocalStorageImpl.getInstance().readObject("user");
        sleep(MEDIUM_SLEEP);
        Assertions.assertEquals(user.getEmail(), storedUser.getEmail());
        Assertions.assertEquals(user.getName(), storedUser.getName());
        Assertions.assertEquals(user.getBirthDate(), storedUser.getBirthDate());
        Assertions.assertTrue(user.getId() > 0);
    }

    @Test
    public void testLogin() throws Exception {
        final User user = user();
        register(user, password());
        logout();
        login(user, password());

        sleep(SHORT_SLEEP);
        User loggedInUser = LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertNotNull(loggedInUser);
        Assertions.assertEquals(user.getEmail(), loggedInUser.getEmail());
    }

    @Test
    public void testLogout() throws Exception {
        final User user = user();
        register(user, password());
        logout();

        for (LocalStorageImpl.LocalStorageFile file : LocalStorageImpl.LocalStorageFile.values()) {
            for (String key : file.getKeys()) {
                if (!key.equals("localSettings")) {
                    Assertions.assertNull(LocalStorageImpl.getInstance().readObject(key));
                }
            }
        }
    }
}

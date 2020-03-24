package org.financer.client.javafx.main;

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
        login(this.user, "wrong");
        sleep(MEDIUM_SLEEP);
        Assertions.assertTrue(find("#loginErrorLabel").isVisible());
    }

    @Test
    public void testRegisterUser() {
        register(this.user, password);
        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        sleep(MEDIUM_SLEEP);
        Assertions.assertEquals(this.user.getEmail(), user.getEmail());
        Assertions.assertEquals(this.user.getName(), user.getName());
        Assertions.assertEquals(this.user.getSurname(), user.getSurname());
        Assertions.assertEquals(this.user.getBirthDate(), user.getBirthDate());
        Assertions.assertTrue(user.getId() > 0);
    }

    @Test
    public void testLogin() throws Exception {
        register(this.user, password);
        logout();
        login(this.user, password);

        sleep(SHORT_SLEEP);
        User loggedInUser = (User) LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertNotNull(loggedInUser);
        Assertions.assertEquals(this.user.getEmail(), loggedInUser.getEmail());
    }

    @Test
    public void testLogout() throws Exception {
        register(this.user, password);
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

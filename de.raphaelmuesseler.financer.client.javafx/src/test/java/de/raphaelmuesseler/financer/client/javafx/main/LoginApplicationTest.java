package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import de.raphaelmuesseler.financer.shared.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

class LoginApplicationTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(LoginApplication.class);
    }

    @Test
    void testInvalidLogin() {
        login(this.user, password);
        Assertions.assertTrue(find("#loginErrorLabel").isVisible());
    }

    @Test
    void testRegisterUser() {
        register(this.user, password);
        User user = (User) LocalStorageImpl.getInstance().readObject("user");
        Assertions.assertEquals(this.user.getEmail(), user.getEmail());
        Assertions.assertEquals(this.user.getName(), user.getName());
        Assertions.assertEquals(this.user.getSurname(), user.getSurname());
        Assertions.assertEquals(1, user.getId());
    }

    @Test
    void testLogin() throws Exception {
        register(this.user, password);
        logout();
        login(this.user, password);

        Assertions.assertFalse(find("#loginErrorLabel").isVisible());
    }

    @Test
    void testLogout() throws Exception {
        register(this.user, password);
        logout();

        for (LocalStorageImpl.LocalStorageFile file : LocalStorageImpl.LocalStorageFile.values()) {
            for (String key : file.getKeys()) {
                Assertions.assertNull(LocalStorageImpl.getInstance().readObject(key));
            }
        }
    }
}

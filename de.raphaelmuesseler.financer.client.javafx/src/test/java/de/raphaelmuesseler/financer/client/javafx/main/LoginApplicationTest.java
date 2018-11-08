package de.raphaelmuesseler.financer.client.javafx.main;

import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import de.raphaelmuesseler.financer.shared.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

class LoginApplicationTest extends AbstractFinancerApplicationTest {

    private final User user = new User(
            "max@mustermann.com",
            "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
            "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
            "Max",
            "Mustermann",
            LocalDate.of(1989, 5, 28));
    private final String password = "password";

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

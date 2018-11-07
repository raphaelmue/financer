package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.main.Server;
import de.raphaelmuesseler.financer.shared.model.User;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

class LoginApplicationTest extends ApplicationTest {

    private final User user = new User(
            "max@mustermann.com",
            "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
            "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
            "Max",
            "Mustermann",
            LocalDate.of(1989, 5, 28));
    private final String password = "password";

    @BeforeAll
    static void setUp() throws Exception {
        Server server = new Server(3500);
        new Thread(server::run).start();

        LocalStorageImpl.getInstance().deleteAllData();

        Database.setDbName(Database.DatabaseName.TEST);
        Database.setHost(true);
        Database.getInstance().clearDatabase();
    }

    @BeforeEach
    void setUpEach() throws Exception {
        LocalStorageImpl.getInstance().deleteAllData();
        ApplicationTest.launch(LoginApplication.class);
    }

    private <T extends Node> T find(final String query) {
        return lookup(query).query();
    }

    @Test
    void testInvalidLogin() {
        clickOn((TextField) find("#loginEmailTextField"));
        write(this.user.getEmail());
        clickOn((TextField) find("#loginPasswordField"));
        write("wrong");
        clickOn((Button) find("#loginBtn"));

        Assertions.assertTrue(find("#loginErrorLabel").isVisible());
    }

    @Test
    void testRegisterUser() {
        clickOn((Hyperlink) find("#openRegisterDialogLink"));

        clickOn((TextField) find("#registerNameTextField"));
        write(this.user.getName());
        clickOn((TextField) find("#registerSurnameTextField"));
        write(this.user.getSurname());
        clickOn((TextField) find("#registerEmailTextField"));
        write(this.user.getEmail());
        clickOn((JFXDatePicker) find("#registerBirthDatePicker"));
        write(this.user.getBirthDateAsLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        clickOn((PasswordField) find("#registerPasswordTextField"));
        write(this.password);
        clickOn((PasswordField) find("#registerRepeatPasswordTextField"));
        write(this.password);
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
    }

    @Test
    void testLogin() {
        clickOn((TextField) find("#loginEmailTextField"));
        write(this.user.getEmail());
        clickOn((TextField) find("#loginPasswordField"));
        write(this.password);
        clickOn((Button) find("#loginBtn"));

        Assertions.assertTrue(find("#loginErrorLabel").isVisible());
    }

    @AfterEach
    void tearDownEach() throws TimeoutException {
        /* Close the window. It will be re-opened at the next test. */
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @AfterAll
    static void tearDown() {
        Server.stop();
    }
}

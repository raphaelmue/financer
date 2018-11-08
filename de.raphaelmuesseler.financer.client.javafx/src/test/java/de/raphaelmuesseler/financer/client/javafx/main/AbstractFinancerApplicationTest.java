package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.main.Server;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

abstract class AbstractFinancerApplicationTest extends ApplicationTest {

    final User user = new User(
            "max@mustermann.com",
            "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
            "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
            "Max",
            "Mustermann",
            LocalDate.of(1989, 5, 28));
    final String password = "password";

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
        Database.getInstance().clearDatabase();
    }

    final <T extends Node> T find(final String query) {
        return lookup(query).query();
    }

    void register(User user, String password) {
        clickOn((Hyperlink) find("#openRegisterDialogLink"));

        clickOn((TextField) find("#registerNameTextField"));
        write(user.getName());
        clickOn((TextField) find("#registerSurnameTextField"));
        write(user.getSurname());
        clickOn((TextField) find("#registerEmailTextField"));
        write(user.getEmail());
        clickOn((JFXDatePicker) find("#registerBirthDatePicker"));
        write(user.getBirthDateAsLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        clickOn((PasswordField) find("#registerPasswordTextField"));
        write(password);
        clickOn((PasswordField) find("#registerRepeatPasswordTextField"));
        write(password);
        confirmDialog();

        sleep(2000);
    }

    void login(User user, String password) {
        clickOn((TextField) find("#loginEmailTextField"));
        write(user.getEmail());
        clickOn((TextField) find("#loginPasswordField"));
        write(password);
        clickOn((Button) find("#loginBtn"));
    }

    void logout() throws Exception {
        FxToolkit.hideStage();
        LocalStorageImpl.getInstance().deleteAllData();
        ApplicationTest.launch(LoginApplication.class);
    }

    void addCategory(Category category) {
        register(this.user, this.password);
        clickOn((Button) find("#profileTabBtn"));
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        Button newCategoryBtn = find("#newCategoryBtn");
        clickOn(I18N.get("fixedExpenses"));
        clickOn(newCategoryBtn);

        JFXTextField categoryNameField = find("#inputDialogTextField");
        categoryNameField.setText("");
        clickOn(categoryNameField);
        write(category.getName());

        confirmDialog();
    }

    final void confirmDialog() {
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
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

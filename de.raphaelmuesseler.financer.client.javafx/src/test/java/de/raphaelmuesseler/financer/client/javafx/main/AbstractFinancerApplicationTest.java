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
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

public class AbstractFinancerApplicationTest extends ApplicationTest {

    final User user = new User(
            "max@mustermann.com",
            "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
            "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
            "Max",
            "Mustermann",
            LocalDate.of(1989, 5, 28));
    final String password = "password";
    private static Server server;

    @BeforeAll
    static void setUp() throws SQLException, IOException {
        server = new Server(3500);
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

    void addCategory(Category category, BaseCategory.CategoryClass categoryClass) {
        register(this.user, this.password);
        clickOn((Button) find("#profileTabBtn"));
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        Button newCategoryBtn = find("#newCategoryBtn");
        clickOn(I18N.get(categoryClass.getName()));
        clickOn(newCategoryBtn);

        JFXTextField categoryNameField = find("#inputDialogTextField");
        categoryNameField.setText("");
        clickOn(categoryNameField);
        write(category.getName());

        confirmDialog();
    }

    void addTransaction(Transaction transaction) {
        addCategory(transaction.getCategoryTree().getValue(), BaseCategory.CategoryClass.VARIABLE_EXPENSES);
        clickOn((Button) find("#transactionsTabBtn"));
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        clickOn((Button) find("#newTransactionBtn"));
        sleep(500);
        TextField amountTextField = find("#amountTextField");
        clickOn(amountTextField);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        press(KeyCode.BACK_SPACE).release(KeyCode.BACK_SPACE);
        write(Double.toString(transaction.getAmount()));
        clickOn((ComboBox) find("#categoryComboBox"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        clickOn((TextField) find("#productTextField"));
        write(transaction.getProduct());
        clickOn((TextField) find("#purposeTextField"));
        write(transaction.getPurpose());
        clickOn((TextField) find("#shopTextField"));
        write(transaction.getShop());
        clickOn((JFXDatePicker) find("#valueDatePicker"));
        write(transaction.getValueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        confirmDialog();
    }

    final void confirmDialog() {
        clickOn("OK");
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
        server.stop();
    }
}

package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.connection.ServerRequest;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.client.javafx.components.IntegerField;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import de.raphaelmuesseler.financer.server.db.Database;
import de.raphaelmuesseler.financer.server.main.Server;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
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
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public class AbstractFinancerApplicationTest extends ApplicationTest {
    private static Server server;

    final String password = "password";
    final User user = new User(
            "max@mustermann.com",
            "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
            "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
            "Max",
            "Mustermann",
            LocalDate.of(1989, 5, 28));
    final CategoryTree category = new CategoryTree(BaseCategory.CategoryClass.VARIABLE_EXPENSES, new Category(-1, "TestCategory", -1, -1));
    final Transaction transaction = new Transaction(-1, 52.5,
            category, "ProductName",
            "Purpose", LocalDate.of(2018, 5, 19), "Shop");
    final FixedTransaction fixedTransaction = new FixedTransaction(-1, 570.0, category, "TestProduct",
            "TestPurpose", LocalDate.of(2018, 2, 5), null, false, 3,
            new ArrayList<>());

    @BeforeAll
    static void setUp() throws SQLException, IOException {
        server = new Server(3505);
        ServerRequest.setPort(3505);
        new Thread(server::run).start();

        LocalStorageImpl.getInstance().deleteAllData();

        Database.setDbName(Database.DatabaseName.TEST);
        Database.setHost(false);
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

    final <T extends Node> T find(Predicate<T> predicate) {
        return lookup(predicate).query();
    }

    void register(User user, String password) {
        clickOn((Hyperlink) find("#openRegisterDialogLink"));

        clickOn((TextField) find("#registerNameTextField"));
        write(user.getName());
        clickOn((TextField) find("#registerSurnameTextField"));
        write(user.getSurname());
        clickOn((TextField) find("#registerEmailTextField"));
        write(user.getEmail());
        JFXDatePicker birthDatePicker = find("#registerBirthDatePicker");
        birthDatePicker.setValue(user.getBirthDateAsLocalDate());
        clickOn((PasswordField) find("#registerPasswordTextField"));
        write(password);
        clickOn((PasswordField) find("#registerRepeatPasswordTextField"));
        write(password);
        confirmDialog();

        sleep(1000);
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

    void addCategory(CategoryTree category) {
        clickOn((Button) find("#profileTabBtn"));
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        Button newCategoryBtn = find("#newCategoryBtn");
        clickOn(I18N.get(category.getCategoryClass().getName()));
        clickOn(newCategoryBtn);

        JFXTextField categoryNameField = find("#inputDialogTextField");
        categoryNameField.setText("");
        clickOn(categoryNameField);
        write(category.getValue().getName());

        confirmDialog();
    }

    void addTransaction(Transaction transaction) {
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
        JFXDatePicker valueDatePicker = find("#valueDatePicker");
        valueDatePicker.setValue(transaction.getValueDate());

        confirmDialog();
    }

    void addFixedTransaction(FixedTransaction fixedTransaction) {
        clickOn((Button) find("#transactionsTabBtn"));
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        sleep(500);
        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategoryTree().getValue().getName())));

        clickOn((Button) find("#newFixedTransactionBtn"));
        sleep(500);
        clickOn((IntegerField) find("#dayTextField"));
        write(Integer.toString(fixedTransaction.getDay()));
        JFXDatePicker datePicker = find("#startDateDatePicker");
        datePicker.setValue(fixedTransaction.getStartDate());
        if (fixedTransaction.isVariable()) {
            clickOn((CheckBox) find("#isVariableCheckbox"));

            for (TransactionAmount transactionAmount : fixedTransaction.getTransactionAmounts()) {
                clickOn((JFXButton) find("#newTransactionAmountBtn"));

                ((JFXDatePicker) find("#valueDatePicker")).setValue(transactionAmount.getValueDate());
                clickOn((DoubleField) find("#amountTextField"));
                eraseText(3);
                write(Double.toString(transactionAmount.getAmount()));
                confirmDialog();
            }
        } else {
            clickOn((TextField) find("#amountTextField"));
            eraseText(3);
            write(Double.toString(fixedTransaction.getAmount()));
        }

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

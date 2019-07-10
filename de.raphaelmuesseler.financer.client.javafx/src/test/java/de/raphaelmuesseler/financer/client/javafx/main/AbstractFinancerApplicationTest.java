package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.connection.ServerRequest;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.components.DoubleField;
import de.raphaelmuesseler.financer.client.javafx.components.IntegerField;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.server.db.DatabaseName;
import de.raphaelmuesseler.financer.server.db.HibernateUtil;
import de.raphaelmuesseler.financer.server.main.Server;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.shared.model.transactions.FixedTransaction;
import de.raphaelmuesseler.financer.shared.model.transactions.TransactionAmount;
import de.raphaelmuesseler.financer.shared.model.transactions.VariableTransaction;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
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
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

class AbstractFinancerApplicationTest extends ApplicationTest {
    private static Server server;

    static final int SHORT_SLEEP = 500;
    static final int MEDIUM_SLEEP = 1000;
    private static final int LONG_SLEEP = 5000;

    final String password = "password";
    final User user = new User(0,
            "max@mustermann.com",
            "6406b2e97a97f64910aca76370ee35a92087806da1aa878e8a9ae0f4dc3949af",
            "I2HoOYJmqKfGboyJAdCEQwulUkxmhVH5",
            "Max",
            "Mustermann",
            LocalDate.of(1989, 5, 28),
            User.Gender.MALE);
    final CategoryTree category = new CategoryTreeImpl(new Category("TestCategory", BaseCategory.CategoryClass.VARIABLE_EXPENSES));
    final VariableTransaction transaction = new VariableTransaction(0,
            52.5,
            LocalDate.of(2018, 5, 19),
            category,
            "ProductName",
            "Purpose",
            "Shop");
    final FixedTransaction fixedTransaction = new FixedTransaction(0,
            570.0,
            category,
            LocalDate.of(2018, 2, 5),
            null,
            "TestProduct",
            "TestPurpose",
            false,
            3,
            new HashSet<>());

    JavaFXFormatter formatter;

    @BeforeAll
    static void setUp() throws IOException {
        server = new Server(3505);
        ServerRequest.setPort(3505);
        new Thread(server::run).start();

        LocalStorageImpl.getInstance().deleteAllData();

        InputStream inputStream = AbstractFinancerApplicationTest.class.getResourceAsStream("/testing.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        HibernateUtil.setIsHostLocal(Boolean.valueOf(properties.getProperty("project.testing.localhost")));
        HibernateUtil.setDatabaseName(DatabaseName.TEST);
    }

    @BeforeEach
    void setUpEach() throws Exception {
        LocalStorageImpl.getInstance().deleteAllData();
        HibernateUtil.cleanDatabase();
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
        birthDatePicker.setValue(user.getBirthDate());
        clickOn((ComboBox) find("#genderComboBox"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        clickOn((PasswordField) find("#registerPasswordTextField"));
        write(password);
        clickOn((PasswordField) find("#registerRepeatPasswordTextField"));
        write(password);
        confirmDialog();

        sleep(LONG_SLEEP);
        formatter = new JavaFXFormatter(LocalStorageImpl.getInstance());
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
//        ApplicationTest.launch(LoginApplication.class);
    }

    void addCategory(CategoryTree category) {
        clickOn((Button) find("#profileTabBtn"));
        sleep(MEDIUM_SLEEP);

        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        Button newCategoryBtn = find("#newCategoryBtn");
        clickOn(I18N.get(category.getValue().getCategoryClass().getName()));
        clickOn(newCategoryBtn);

        JFXTextField categoryNameField = find("#inputDialogTextField");
        categoryNameField.setText("");
        clickOn(categoryNameField);
        write(category.getValue().getName());

        confirmDialog();

        sleep(MEDIUM_SLEEP);
    }

    void addTransaction(VariableTransaction transaction) {
        clickOn((Button) find("#transactionsTabBtn"));
        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        clickOn((Button) find("#newTransactionBtn"));
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

        sleep(MEDIUM_SLEEP);
        category.getValue().setId(TreeUtil.getByValue((BaseCategory) LocalStorageImpl.getInstance().readObject("categories"),
                category, Comparator.comparing(Category::getName)).getValue().getId());
    }

    void addFixedTransaction(FixedTransaction fixedTransaction) {
        clickOn((Button) find("#transactionsTabBtn"));
        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategoryTree().getValue().getName())));

        clickOn((Button) find("#newFixedTransactionBtn"));
        sleep(SHORT_SLEEP);
        clickOn((IntegerField) find("#dayTextField"));
        write(Integer.toString(fixedTransaction.getDay()));
        JFXDatePicker datePicker = find("#startDateDatePicker");
        datePicker.setValue(fixedTransaction.getStartDate());
        if (fixedTransaction.getIsVariable()) {
            clickOn((CheckBox) find("#isVariableCheckbox"));

            sleep(100);

            for (TransactionAmount transactionAmount : fixedTransaction.getTransactionAmounts()) {
                clickOn((JFXButton) find("#newTransactionAmountBtn"));

                ((JFXDatePicker) find("#transactionAmountValueDatePicker")).setValue(transactionAmount.getValueDate());
                clickOn((DoubleField) find("#transactionAmountTextField"));
                eraseText(3);
                write(Double.toString(transactionAmount.getAmount()));
                press(KeyCode.TAB).release(KeyCode.TAB);
                press(KeyCode.ENTER).release(KeyCode.ENTER);
            }
        } else {
            clickOn((TextField) find("#amountTextField"));
            eraseText(3);
            write(Double.toString(fixedTransaction.getAmount()));
        }
        clickOn((JFXTextField) find("#productTextField"));
        write(fixedTransaction.getProduct());
        clickOn((JFXTextField) find("#purposeTextField"));
        write(fixedTransaction.getPurpose());

        confirmDialog();

        sleep(SHORT_SLEEP);
    }

    final CategoryTree getCategoryTree() {
        return ((CategoryTree) TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories"))
                        .getCategoryTreeByCategoryClass(fixedTransaction.getCategoryTree().getValue().getCategoryClass()),
                fixedTransaction.getCategoryTree(), (o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName())));
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

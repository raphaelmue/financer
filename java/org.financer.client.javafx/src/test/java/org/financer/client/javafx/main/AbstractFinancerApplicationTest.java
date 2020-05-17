package org.financer.client.javafx.main;

import com.jfoenix.controls.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.transaction.*;
import org.financer.client.domain.model.user.Token;
import org.financer.client.domain.model.user.User;
import org.financer.client.domain.model.user.VerificationToken;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.DatePicker;
import org.financer.client.javafx.components.DoubleField;
import org.financer.client.javafx.components.IntegerField;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.domain.model.value.objects.*;
import org.financer.util.collections.TreeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public abstract class AbstractFinancerApplicationTest extends ApplicationTest {

    static final int SHORT_SLEEP = 500;
    static final int MEDIUM_SLEEP = 1000;
    private static final int LONG_SLEEP = 5000;

    JavaFXFormatter formatter;

    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
    }

    protected User user() {
        return new User()
                .setId(1)
                .setEmail(new Email("test@test.com"))
                .setName(new Name("Test", "User"))
                .setPassword(new HashedPassword(password()))
                .setTokens(new HashSet<>(Collections.singletonList(token())))
                .setVerified(false);
    }

    protected String password() {
        return "password";
    }

    protected Token token() {
        return new Token()
                .setId(1)
                .setExpireDate(new ExpireDate())
                .setIpAddress(new IPAddress("192.168.0.1"))
                .setToken(tokenString())
                .setOperatingSystem(new OperatingSystem(OperatingSystem.Values.LINUX));
    }

    protected VerificationToken verificationToken() {
        return new VerificationToken()
                .setId(1)
                .setExpireDate(new ExpireDate(LocalDate.now().plusDays(15)))
                .setToken(tokenString());
    }

    protected TokenString tokenString() {
        return new TokenString("Z6XCS3tyyBlhPfsv7rMxLgfdyEOlUkv0CWSdbFYHCNX2wLlwpH97lJNP69Bny3XZ");
    }

    protected Category variableCategory() {
        return new Category()
                .setId(1)
                .setUser(user())
                .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                .setName("Variable Category")
                .setParent(null);
    }

    protected Category variableCategoryParent() {
        return new Category()
                .setId(2)
                .setUser(user())
                .setCategoryClass(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                .setName("Variable Category Parent")
                .setParent(null);
    }

    protected Category fixedCategory() {
        return new Category()
                .setId(2)
                .setUser(user())
                .setCategoryClass(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES))
                .setName("Fixed Category")
                .setParent(null);
    }

    protected Product product() {
        return new Product()
                .setId(1)
                .setName("Test Product")
                .setQuantity(new Quantity(2))
                .setAmount(new Amount(50));
    }

    protected VariableTransaction variableTransaction() {
        return new VariableTransaction()
                .setId(1)
                .setValueDate(new ValueDate())
                .setCategory(variableCategory())
                .setDescription("Test Purpose")
                .setVendor("Test Vendor")
                .addProduct(product());
    }

    protected Attachment attachment() {
        return new Attachment()
                .setId(1)
                .setName("test.pdf")
                .setTransaction(variableTransaction())
                .setUploadDate(LocalDate.now());
    }

    protected FixedTransactionAmount fixedTransactionAmount() {
        return new FixedTransactionAmount()
                .setId(1)
                .setAmount(new Amount(50))
                .setValueDate(new ValueDate());
    }

    protected FixedTransaction fixedTransaction() {
        return new FixedTransaction()
                .setId(2)
                .setCategory(fixedCategory())
                .setTimeRange(new TimeRange())
                .setIsVariable(false)
                .setAmount(new Amount(50.0))
                .setDay(1)
                .setDescription("Test Purpose")
                .setVendor("Test Vendor")
                .addFixedTransactionAmount(fixedTransactionAmount());
    }

    @BeforeEach
    void setUpEach() throws Exception {
    }

    final <T extends Node> T find(final String query) {
        return lookup(query).query();
    }

    final <T extends Node> T find(Predicate<T> predicate) {
        return lookup(predicate).query();
    }

    void register(User user, String password) {
        clickOn((JFXButton) find("#openRegisterDialogBtn"));

        clickOn((JFXTextField) find("#registerNameTextField"));
        write(user.getName().getFirstName());
        clickOn((JFXTextField) find("#registerSurnameTextField"));
        write(user.getName().getSurname());
        clickOn((JFXTextField) find("#registerEmailTextField"));
        write(user.getEmail().getEmailAddress());
        DatePicker birthDatePicker = find("#registerBirthDatePicker");
        birthDatePicker.setValue(user.getBirthDate().getBirthDate());
        clickOn((JFXComboBox) find("#genderComboBox"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        clickOn((JFXPasswordField) find("#registerPasswordTextField"));
        write(password);
        clickOn((JFXPasswordField) find("#registerRepeatPasswordTextField"));
        write(password);
        confirmDialog();

        sleep(LONG_SLEEP);
        formatter = new JavaFXFormatter(LocalStorageImpl.getInstance());
    }

    void login(User user, String password) {
        clickOn((TextField) find("#loginEmailTextField"));
        write(user.getEmail().getEmailAddress());
        clickOn((TextField) find("#loginPasswordField"));
        write(password);
        clickOn((Button) find("#loginBtn"));
    }

    void logout() throws Exception {
        clickOn(formatter.format(user().getName()));
        clickOn("Logout");
    }

    void addCategory(Category category) {
        clickOn((JFXButton) find("#profileTabBtn"));
        sleep(MEDIUM_SLEEP);

        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        Button newCategoryBtn = find("#newCategoryBtn");
        clickOn(I18N.get(category.getCategoryClass().getCategoryClass().getName()));
        clickOn(newCategoryBtn);

        JFXTextField categoryNameField = find("#inputDialogTextField");
        categoryNameField.setText("");
        clickOn(categoryNameField);
        write(category.getName());

        confirmDialog();

        sleep(MEDIUM_SLEEP);
    }

    void addVariableTransaction(VariableTransaction transaction) {
        clickOn((JFXButton) find("#transactionsTabBtn"));
        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        clickOn((JFXButton) find("#newTransactionBtn"));
        DoubleField amountTextField = find("#amountTextField");
        clickOn(amountTextField);
        eraseText(3);
        write(Double.toString(transaction.getAmount().getAmount()));
        clickOn((JFXComboBox) find("#categoryComboBox"));
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        clickOn((JFXTextField) find("#purposeTextField"));
        write(transaction.getDescription());
        clickOn((JFXTextField) find("#shopTextField"));
        write(transaction.getVendor());
        DatePicker valueDatePicker = find("#valueDatePicker");
        valueDatePicker.setValue(transaction.getValueDate().getDate());

        confirmDialog();

        sleep(MEDIUM_SLEEP);
        transaction.getCategory().setId(((Category) TreeUtil.getByValue(LocalStorageImpl.getInstance().readObject("categories"),
                transaction.getCategory(), Comparator.comparing(Category::getName))).getId());
    }

    void addFixedTransaction(FixedTransaction fixedTransaction) {
        clickOn((JFXButton) find("#transactionsTabBtn"));
        sleep(MEDIUM_SLEEP);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);
        press(KeyCode.RIGHT).release(KeyCode.RIGHT);

        clickOn(find((Label label) -> label.getText().contains(fixedTransaction.getCategory().getName())));

        clickOn((JFXButton) find("#newFixedTransactionBtn"));
        sleep(SHORT_SLEEP);
        clickOn((IntegerField) find("#dayTextField"));
        eraseText(1);
        write(Integer.toString(fixedTransaction.getDay()));
        DatePicker datePicker = find("#startDateDatePicker");
        datePicker.setValue(fixedTransaction.getTimeRange().getStartDate());
        if (fixedTransaction.getIsVariable()) {
            clickOn((JFXCheckBox) find("#isVariableCheckbox"));

            sleep(100);

            for (FixedTransactionAmount transactionAmount : fixedTransaction.getTransactionAmounts()) {
                clickOn((JFXButton) find("#newTransactionAmountBtn"));

                ((DatePicker) find("#transactionAmountValueDatePicker")).setValue(transactionAmount.getValueDate().getDate());
                clickOn((DoubleField) find("#transactionAmountTextField"));
                eraseText(3);
                write(Double.toString(transactionAmount.getAmount().getAmount()));
                press(KeyCode.TAB).release(KeyCode.TAB);
                press(KeyCode.ENTER).release(KeyCode.ENTER);
            }
        } else {
            clickOn((DoubleField) find("#amountTextField"));
            eraseText(3);
            write(Double.toString(fixedTransaction.getAmount().getAmount()));
        }
        clickOn((JFXTextField) find("#productTextField"));
        write(fixedTransaction.getProduct());
        clickOn((JFXTextField) find("#purposeTextField"));
        write(fixedTransaction.getDescription());

        confirmDialog();

        sleep(SHORT_SLEEP);
    }

    final Category getCategoryTree() {
        return variableCategory();
    }

    final void confirmDialog() {
        clickOn("Ok");
    }

    @AfterEach
    void tearDownEach() throws TimeoutException {
        /* Close the window. It will be re-opened at the next test. */
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }
}

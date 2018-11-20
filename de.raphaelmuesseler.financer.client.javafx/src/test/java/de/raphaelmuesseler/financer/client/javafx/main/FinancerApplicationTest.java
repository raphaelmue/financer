package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import sun.jvm.hotspot.utilities.Assert;

import java.time.LocalDate;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

public class FinancerApplicationTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
    }

    // ------------------- CATEGORIES ------------------- \\

    private final Category category = new Category(-1, "TestCategory", -1, -1);
    private final Transaction transaction = new Transaction(-1, 52.5,
            new CategoryTree(BaseCategory.CategoryClass.VARIABLE_EXPENSES, category), "ProductName",
            "Purpose", LocalDate.of(2018, 5, 19), "Shop");

    @Test
    public void testCreateCategory() {
        addCategory(category, BaseCategory.CategoryClass.FIXED_EXPENSES);
        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
                new CategoryTree(BaseCategory.CategoryClass.FIXED_EXPENSES, category),
                (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
        Assertions.assertNotNull(categoryTree);
    }

    @Test
    public void testEditCategory() {
        addCategory(category, BaseCategory.CategoryClass.FIXED_EXPENSES);
        sleep(500);
        clickOn(category.getName());
        clickOn((Button) find("#editCategoryBtn"));
        category.setName("Category 2");
        JFXTextField categoryNameField = find("#inputDialogTextField");
        categoryNameField.setText("");
        clickOn(categoryNameField);
        write(category.getName());
        confirmDialog();
        sleep(500);

        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
                new CategoryTree(BaseCategory.CategoryClass.FIXED_EXPENSES, category),
                (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
        Assertions.assertNotNull(categoryTree);
    }

    @Test
    public void testDeleteCategory() {
        addCategory(category, BaseCategory.CategoryClass.FIXED_EXPENSES);
        sleep(500);
        clickOn(category.getName());
        clickOn((Button) find("#deleteCategoryBtn"));
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(500);
        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
                new CategoryTree(BaseCategory.CategoryClass.FIXED_EXPENSES, category),
                (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
        Assertions.assertNull(categoryTree);
    }

    // ------------------- TRANSACTIONS ------------------- \\

    @Test
    public void testAddTransaction() {
        addTransaction(transaction);
        clickOn((Button) find("#refreshTransactionsBtn"));
        sleep(2000);

        Assertions.assertEquals(1, LocalStorageImpl.getInstance().readList("transactions").size());

        Transaction insertedTransaction = (Transaction) LocalStorageImpl.getInstance().readList("transactions").get(0);
        Assertions.assertEquals(-transaction.getAmount(), insertedTransaction.getAmount());
        Assertions.assertEquals(transaction.getProduct(), insertedTransaction.getProduct());
        Assertions.assertEquals(transaction.getValueDate(), insertedTransaction.getValueDate());
        Assertions.assertEquals(transaction.getPurpose(), insertedTransaction.getPurpose());
        Assertions.assertEquals(transaction.getShop(), insertedTransaction.getShop());
        Assertions.assertEquals(transaction.getCategoryTree().getValue().getName(), insertedTransaction.getCategoryTree().getValue().getName());
    }
}
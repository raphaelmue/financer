package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

class FinancerApplicationTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
    }

    // ------------------- CATEGORIES ------------------- \\

    private final Category category = new Category(-1, "TestCategory", -1, -1);

    @Test
    void testCreateCategory() {
        addCategory(category);
        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
                new CategoryTree(BaseCategory.CategoryClass.FIXED_EXPENSES, category),
                (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
        Assertions.assertNotNull(categoryTree);
    }

    @Test
    void testEditCategory() {
        addCategory(category);
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
    void testDeleteCategory() {
        addCategory(category);
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
}
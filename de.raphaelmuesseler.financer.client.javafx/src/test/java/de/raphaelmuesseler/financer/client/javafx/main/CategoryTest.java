package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class CategoryTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
    }

    @Test
    public void testCreateCategory() {
        register(this.user, this.password);
        addCategory(category);
        sleep(500);
        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
                category, (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
        Assertions.assertNotNull(clickOn(category.getValue().getName()));
        Assertions.assertNotNull(categoryTree);
    }

    @Test
    public void testEditCategory() {
        register(this.user, this.password);
        addCategory(category);
        sleep(500);
        clickOn(category.getValue().getName());
        clickOn((Button) find("#editCategoryBtn"));
        category.getValue().setName("Category 2");
        JFXTextField categoryNameField = find("#inputDialogTextField");
        categoryNameField.setText("");
        clickOn(categoryNameField);
        write(category.getValue().getName());
        confirmDialog();
        sleep(500);

        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
               category, (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
        Assertions.assertNotNull(categoryTree);
    }

    @Test
    public void testDeleteCategory() {
        register(this.user, this.password);
        addCategory(category);
        sleep(500);
        clickOn(category.getValue().getName());
        clickOn((Button) find("#deleteCategoryBtn"));
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(1000);
        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
               category, (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
        Assertions.assertNull(categoryTree);
    }

    // ------------------- TRANSACTIONS ------------------- \\


}
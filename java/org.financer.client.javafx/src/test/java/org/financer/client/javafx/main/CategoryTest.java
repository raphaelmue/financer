package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Button;
import org.financer.client.domain.model.category.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class CategoryTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testCreateCategory() {
        register(user(), password());
        addCategory(variableCategory());
        Assertions.assertNotNull(clickOn(variableCategory().getName()));
        Assertions.assertNotNull(getCategoryTree());
    }

    @Test
    public void testEditCategory() {
        Category category = variableCategory();
        register(user(), password());
        addCategory(category);
        clickOn(category.getName());
        clickOn((Button) find("#editCategoryBtn"));
        sleep(MEDIUM_SLEEP);
        clickOn((JFXTextField) find("#inputDialogTextField"));
        eraseText(category.getName().length());
        category.setName("Category 2");
        write(category.getName());
        confirmDialog();
        sleep(MEDIUM_SLEEP);
//        Tree categoryTree = TreeUtil.getByValue(((CategoryRoot) LocalStorageImpl.getInstance().readObject("categories")),
//                category, (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
//        Assertions.assertNotNull(categoryTree);
    }

    @Test
    public void testDeleteCategory() {
        Category category = variableCategory();
        register(user(), password());
        addCategory(category);
        clickOn(category.getName());
        clickOn((Button) find("#deleteCategoryBtn"));
        confirmDialog();
        sleep(MEDIUM_SLEEP);
//        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
//                category, (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
//        Assertions.assertNull(categoryTree);
    }
}
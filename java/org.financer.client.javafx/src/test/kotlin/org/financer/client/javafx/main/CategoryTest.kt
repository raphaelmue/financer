package org.financer.client.javafx.main

import com.jfoenix.controls.JFXTextField
import javafx.scene.Node
import javafx.scene.control.Button
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
class CategoryTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    public override fun setUpEach() {
        super.setUpEach()
    }

    @Test
    fun testCreateCategory() {
        register(user(), password())
        addCategory(variableCategory())
        Assertions.assertNotNull(clickOn(variableCategory().name))
        Assertions.assertNotNull(categoryTree)
    }

    @Test
    fun testEditCategory() {
        val category = variableCategory()
        register(user(), password())
        addCategory(category!!)
        clickOn(category.name)
        clickOn(find<Node>("#editCategoryBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        clickOn(find<Node>("#inputDialogTextField") as JFXTextField)
        eraseText(category.name!!.length)
        category.setName("Category 2")
        write(category.name)
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        //        Tree categoryTree = TreeUtil.getByValue(((CategoryRoot) LocalStorageImpl.getInstance().readObject("categories")),
//                category, (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
//        Assertions.assertNotNull(categoryTree);
    }

    @Test
    fun testDeleteCategory() {
        val category = variableCategory()
        register(user(), password())
        addCategory(category!!)
        clickOn(category.name)
        clickOn(find<Node>("#deleteCategoryBtn") as Button)
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        //        Tree<Category> categoryTree = TreeUtil.getByValue(((BaseCategory) LocalStorageImpl.getInstance().readObject("categories")),
//                category, (o1, o2) -> CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()));
//        Assertions.assertNull(categoryTree);
    }
}
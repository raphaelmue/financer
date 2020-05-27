package org.financer.client.javafx.main.profile

import com.jfoenix.controls.JFXButton
import javafx.application.Platform
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTreeCell
import javafx.util.StringConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.category.CategoryRoot
import org.financer.client.domain.model.user.User
import org.financer.client.format.Formatter
import org.financer.client.format.FormatterImpl
import org.financer.client.format.I18N.get
import org.financer.client.javafx.dialogs.FinancerConfirmDialog
import org.financer.client.javafx.dialogs.FinancerTextInputDialog
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.javafx.main.FinancerController
import org.financer.client.local.Application
import org.financer.shared.domain.model.value.objects.CategoryClass
import org.financer.util.collections.Action
import org.financer.util.collections.Tree
import org.financer.util.collections.TreeUtil
import java.net.URL
import java.util.*
import java.util.logging.Logger

class ProfileController : Initializable {
    @FXML
    var fullNameLabel: Label? = null

    @FXML
    var emailLabel: Label? = null

    @FXML
    var birthDateLabel: Label? = null

    @FXML
    var addressLabel: Label? = null

    @FXML
    var genderLabel: Label? = null

    @FXML
    var changePasswordLink: Hyperlink? = null

    @FXML
    var categoriesTreeView: TreeView<Category>? = null

    @FXML
    var refreshCategoriesBtn: JFXButton? = null

    @FXML
    var newCategoryBtn: JFXButton? = null

    @FXML
    var editCategoryBtn: JFXButton? = null

    @FXML
    var deleteCategoryBtn: JFXButton? = null

    @FXML
    var editPersonalInformationBtn: JFXButton? = null
    private val restApi: RestApi = RestApiImpl()
    private val localStorage = LocalStorageImpl.getInstance()
    private val formatter: Formatter = FormatterImpl(localStorage)
    private lateinit var user: User
    private val logger = Logger.getLogger("FinancerApplication")
    private lateinit var categoryRoot: CategoryRoot
    private var treeStructure: TreeItem<Category>? = null
    override fun initialize(location: URL, resources: ResourceBundle) {
        CoroutineScope(Dispatchers.Default).launch {
            FinancerController.instance!!.showLoadingBox()
            user = localStorage.readObject("user") ?: throw IllegalStateException("No User stored in local storage.")
            withContext(Dispatchers.Main) { fillPersonalInformationLabels() }
            changePasswordLink!!.onAction = EventHandler {
                val dialog = ChangePasswordDialog(user)
                dialog.setOnConfirm(Action { updatedUser: User? ->
                    CoroutineScope(Dispatchers.IO).launch {
                        restApi.updateUsersPassword(user.getId(), updatedUser!!.password!!)
                        FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succChangedPassword"))
                    }
                })
            }

            CoroutineScope(Dispatchers.IO).launch {
                val updateUser = restApi.updateUsersPersonalInformation(user)
                if (updateUser != null) {
                    FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succChangedPersonalInformation"))
                    user = updateUser
                    localStorage.writeObject("user", updateUser)
                }
            }
            val fontAwesome = GlyphFontRegistry.font("FontAwesome")
            refreshCategoriesBtn!!.graphic = fontAwesome.create(FontAwesome.Glyph.REFRESH)
            refreshCategoriesBtn!!.graphicTextGap = 8.0
            newCategoryBtn!!.graphic = fontAwesome.create(FontAwesome.Glyph.PLUS)
            newCategoryBtn!!.graphicTextGap = 8.0
            newCategoryBtn!!.isDisable = true
            editCategoryBtn!!.graphic = fontAwesome.create(FontAwesome.Glyph.EDIT)
            editCategoryBtn!!.graphicTextGap = 8.0
            editCategoryBtn!!.isDisable = true
            deleteCategoryBtn!!.graphic = fontAwesome.create(FontAwesome.Glyph.TRASH)
            deleteCategoryBtn!!.graphicTextGap = 8.0
            deleteCategoryBtn!!.isDisable = true
            handleRefreshCategories()
            editPersonalInformationBtn!!.onAction = EventHandler { event: ActionEvent? ->
                val dialog = ChangePersonalInformationDialog(user)
                dialog.setOnConfirm(Action { user: User? ->
                    if (user != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val updatedUser = restApi.updateUsersPersonalInformation(user)
                            if (updatedUser != null) {
                                FinancerController.instance!!.showToast(Application.MessageType.SUCCESS, get("succChangedPersonalInformation"))
                                localStorage.writeObject("user", updatedUser)
                                withContext(Dispatchers.Main) { fillPersonalInformationLabels() }
                            }
                        }
                    }
                })
            }
            newCategoryBtn!!.isDisable = true
            editCategoryBtn!!.isDisable = true
            deleteCategoryBtn!!.isDisable = true
            categoryRoot = localStorage.readObject("categories")
                    ?: throw IllegalStateException("No categories stored in local storage.");
            loadCategoryData()
            FinancerController.instance!!.hideLoadingBox()
        }
    }

    private fun fillPersonalInformationLabels() {
        fullNameLabel!!.text = formatter.format(user.name!!)
        emailLabel!!.text = user.email!!.emailAddress
        birthDateLabel!!.text = formatter.format(user.birthDate!!)
        genderLabel!!.text = get(user.gender!!.gender.getName())
    }

    fun handleRefreshCategories() = CoroutineScope(Dispatchers.IO).launch {
        val result = restApi.getUsersCategories(user.getId())
        categoryRoot = CategoryRoot(result.toSet())
        localStorage.writeObject("categories", categoryRoot)
        withContext(Dispatchers.Main) { loadCategoryData() }
    }

    private fun loadCategoryData() {
        createTreeView()
        categoriesTreeView!!.selectionModel.selectedItemProperty().addListener { observableValue: ObservableValue<out TreeItem<Category>?>?, oldValue: TreeItem<Category>?, newValue: TreeItem<Category>? ->
            if (newValue != null) {
                newCategoryBtn!!.isDisable = false
                editCategoryBtn!!.isDisable = newValue.value.isRoot
                deleteCategoryBtn!!.isDisable = newValue.value.isRoot
            }
        }
        categoriesTreeView!!.isEditable = false
        categoriesTreeView!!.isShowRoot = false
        Platform.runLater { categoriesTreeView!!.setRoot(treeStructure) }
        expandTreeView(treeStructure)
        categoriesTreeView!!.setCellFactory { param: TreeView<Category>? -> cellFactory }
        categoriesTreeView!!.setOnEditCommit { event: TreeView.EditEvent<Category> ->
            event.newValue.setId(event.oldValue.id)
            event.newValue.parent = event.oldValue.parent
            event.newValue.setCategoryClass(event.oldValue.categoryClass)
            handleUpdateCategory(event.newValue)
        }
    }

    fun handleNewCategory() {
        this.handleNewCategory(categoriesTreeView!!.selectionModel.selectedItem)
    }

    private fun handleNewCategory(currentItem: TreeItem<Category>?) {
        if (currentItem != null) {
            val dialog = FinancerTextInputDialog(get("enterCategoryName"), get("newCategory"))
            dialog.setOnConfirm(Action { categoryName: String? ->
                val newCategory = Category()
                        .setParent(currentItem.value)
                        .setCategoryClass(currentItem.value.categoryClass)
                        .setName(categoryName)
                CoroutineScope(Dispatchers.IO).launch {
                    val result = restApi.createCategory(newCategory)
                    if (result != null) {
                        currentItem.children.add(TreeItem(result))
                        if (categoriesTreeView!!.selectionModel.selectedItem.value.isRoot) {
                            categoryRoot.getCategoriesByClass(newCategory.categoryClass!!).add(newCategory)
                        } else {
                            TreeUtil.insertByValue(categoryRoot, newCategory) { o1: Tree, o2: Tree -> (o1 as Category).parent.id.compareTo((o2 as Category).id) }
                        }
                        localStorage.writeObject("categories", categoryRoot)
                        Platform.runLater {
                            categoriesTreeView!!.selectionModel.selectedItem.isExpanded = true
                            categoriesTreeView!!.refresh()
                        }
                    }

                }
            })
        }
    }

    fun handleEditCategory() {
        if (categoriesTreeView!!.selectionModel.selectedItem != null) {
            val category = categoriesTreeView!!.selectionModel.selectedItem.value
            val dialog = FinancerTextInputDialog(get("enterCategoryName"), category.name)
            dialog.setOnConfirm(Action { categoryName: String? ->
                category.setName(categoryName)
                categoriesTreeView!!.refresh()
                handleUpdateCategory(category)
            })
        }
    }

    private fun handleUpdateCategory(category: Category) = CoroutineScope(Dispatchers.IO).launch {
        restApi.updateCategory(category)
        categoriesTreeView!!.selectionModel.selectedItem.value.setName(category.name)
        localStorage.writeObject("categories", categoryRoot)
    }

    fun handleDeleteCategory() {
        this.handleDeleteCategory(categoriesTreeView!!.selectionModel.selectedItem.value)
    }

    private fun handleDeleteCategory(category: Category?) {
        if (category != null && !category.isRoot) {
            val dialog = FinancerConfirmDialog(get("confirmDeleteCategory"))
            dialog.setOnConfirm(Action {
                CoroutineScope(Dispatchers.IO).launch {
                    restApi.deleteCategory(category.id)
                    TreeUtil.deleteByValue(categoryRoot, categoriesTreeView!!.selectionModel.selectedItem.value)
                    localStorage.writeObject("categories", categoryRoot)
                    Platform.runLater { categoriesTreeView!!.selectionModel.selectedItem.parent.children.remove(categoriesTreeView!!.selectionModel.selectedItem) }
                }
            })
        }
    }

    private fun createTreeView() {
        treeStructure = TreeItem(Category().setName("root"))
        for (categoryClass in CategoryClass.getAll()) {
            val categories = categoryRoot.getCategoriesByClass(categoryClass)
            for (category in categories) {
                this.createTreeView(treeStructure!!, category)
            }
        }
    }

    private fun createTreeView(treeItem: TreeItem<Category>, root: Category) {
        val treeRoot = TreeItem(root)
        treeItem.children.add(treeRoot)
        if (!root.isLeaf) {
            for (child in root.children) {
                createTreeView(treeRoot, child)
            }
        }
    }

    private val cellFactory: TextFieldTreeCellImpl = TextFieldTreeCellImpl(object : StringConverter<Category>() {
        override fun toString(category: Category): String {
            return (if (category.name == category.categoryClass!!.categoryClass.getName()) get(category.name) else category.name)!!
        }

        override fun fromString(string: String): Category? {
            return Category().setName(string)
        }
    })

    private fun expandTreeView(item: TreeItem<*>?) {
        if (item != null && !item.isLeaf) {
            item.isExpanded = true
            for (child in item.children) {
                expandTreeView(child)
            }
        }
    }

    private inner class TextFieldTreeCellImpl internal constructor(stringConverter: StringConverter<Category>)
        : TextFieldTreeCell<Category>(stringConverter) {
        private val customContextMenu = ContextMenu()
        private val deleteContextMenu = ContextMenu()
        override fun updateItem(item: Category?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item != null && !isEditing && parent != null) {
                if (item.isRoot && formatter.format(item) != get("categories")) {
                    contextMenu = this.customContextMenu
                } else if (!item.isRoot) {
                    contextMenu = deleteContextMenu
                }
            }
        }

        init {
            val addMenuItem = MenuItem(get("new"))
            addMenuItem.onAction = EventHandler { t: ActionEvent? -> handleNewCategory() }
            this.customContextMenu.items.add(addMenuItem)
            val addMenuItemDelete = MenuItem(get("new"))
            addMenuItemDelete.onAction = EventHandler { t: ActionEvent? -> treeItem.children.add(TreeItem(Category().setName("newCategory"))) }
            deleteContextMenu.items.add(addMenuItemDelete)
            val deleteMenuItem = MenuItem(get("delete"))
            deleteContextMenu.items.add(deleteMenuItem)
            deleteMenuItem.onAction = EventHandler { t: ActionEvent? -> handleDeleteCategory(treeItem.value) }
        }
    }
}
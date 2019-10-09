package de.raphaelmuesseler.financer.client.javafx.main.profile;

import com.jfoenix.controls.JFXButton;
import de.raphaelmuesseler.financer.client.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerConfirmDialog;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerTextInputDialog;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerController;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTreeImpl;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.collections.Tree;
import de.raphaelmuesseler.financer.util.collections.TreeUtil;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;

import java.io.Serializable;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ProfileController implements Initializable {

    @FXML
    public Label fullNameLabel;
    @FXML
    public Label emailLabel;
    @FXML
    public Label birthDateLabel;
    @FXML
    public Label addressLabel;
    @FXML
    public Label genderLabel;
    @FXML
    public Hyperlink changePasswordLink;
    @FXML
    public TreeView<CategoryTree> categoriesTreeView;
    @FXML
    public JFXButton refreshCategoriesBtn;
    @FXML
    public JFXButton newCategoryBtn;
    @FXML
    public JFXButton editCategoryBtn;
    @FXML
    public JFXButton deleteCategoryBtn;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private BaseCategory categories;
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private TreeItem<CategoryTree> treeStructure;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.setInitializationThread(new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            this.user = (User) this.localStorage.readObject("user");
            if (user != null) {
                Platform.runLater(this::initializePersonalInformation);
            }

            this.changePasswordLink.setOnAction(event -> {
                ChangePasswordDialog dialog = new ChangePasswordDialog(user);
                dialog.setOnConfirm(user -> {

                    Map<String, Serializable> parameters = new HashMap<>();
                    parameters.put("user", user);

                    FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "changePassword", parameters,
                            new AsyncConnectionCall() {
                                @Override
                                public void onSuccess(ConnectionResult result) {
                                    FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succChangedPassword"));
                                }

                                @Override
                                public void onAfter() {
                                    localStorage.writeObject("user", user);
                                }
                            }));
                });
            });

            this.newCategoryBtn.setDisable(true);
            this.editCategoryBtn.setDisable(true);
            this.deleteCategoryBtn.setDisable(true);

            categories = (BaseCategory) localStorage.readObject("categories");
            this.loadCategoryData();
            FinancerController.getInstance().hideLoadingBox();
        }));
        FinancerController.getInitializationThread().start();
    }

    public void handleRefreshCategories() {
        RetrievalServiceImpl.getInstance().fetchCategories(this.user, new AsyncCall<>() {
            @Override
            public void onSuccess(BaseCategory result) {
                categories = result;
            }

            @Override
            public void onFailure(Exception exception) {
                categories = (BaseCategory) localStorage.readObject("categories");
            }

            @Override
            public void onAfter() {
                Platform.runLater(() -> loadCategoryData());
            }
        });
    }

    private void loadCategoryData() {
        createTreeView();
        categoriesTreeView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                newCategoryBtn.setDisable(false);

                editCategoryBtn.setDisable(newValue.getValue().isRoot());
                deleteCategoryBtn.setDisable(newValue.getValue().isRoot());
            }
        });
        categoriesTreeView.setEditable(false);
        categoriesTreeView.setShowRoot(false);
        categoriesTreeView.setRoot(treeStructure);
        expandTreeView(treeStructure);
        categoriesTreeView.setCellFactory(param -> getCellFactory());
        categoriesTreeView.setOnEditCommit(event -> {
            event.getNewValue().getValue().setId(event.getOldValue().getValue().getId());
            event.getNewValue().getValue().setParentId(event.getOldValue().getValue().getParentId());
            event.getNewValue().getValue().setCategoryClass(event.getOldValue().getValue().getCategoryClass());
            handleUpdateCategory(event.getNewValue());
        });
    }

    private void initializePersonalInformation() {
        this.fullNameLabel.setText(user.getFullName());
        this.emailLabel.setText(user.getEmail());
        this.birthDateLabel.setText(new JavaFXFormatter(localStorage).formatDate(this.user.getBirthDate()));
        this.genderLabel.setText(I18N.get(this.user.getGender().getName()));
    }

    public void handleNewCategory() {
        this.handleNewCategory(this.categoriesTreeView.getSelectionModel().getSelectedItem());
    }

    private void handleNewCategory(TreeItem<CategoryTree> currentItem) {
        if (currentItem != null) {
            FinancerTextInputDialog dialog = new FinancerTextInputDialog(I18N.get("enterCategoryName"), I18N.get("newCategory"));
            dialog.setOnConfirm(categoryName -> {
                CategoryTree categoryTree = new CategoryTreeImpl(
                        categoriesTreeView.getSelectionModel().getSelectedItem().getValue(),
                        new Category(0, categoryName, currentItem.getValue().getValue().getId(), currentItem.getValue().getValue().getCategoryClass()));

                Map<String, Serializable> parameters = new HashMap<>();
                categoryTree.getValue().setUser(user.toEntity());
                parameters.put("category", categoryTree.getValue());

                FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "addCategory", parameters, result -> {
                    categoryTree.getValue().setId(((Category) result.getResult()).getId());
                    categoryTree.getValue().setPrefix(categoryTree.getParent().getValue().getPrefix() + (categoryTree.getParent().getChildren().size() + 1) + ".");
                    if (categoriesTreeView.getSelectionModel().getSelectedItem().getValue().isRoot()) {
                        categories.getCategoryTreeByCategoryClass(categoriesTreeView.getSelectionModel().getSelectedItem().getValue().getValue().getCategoryClass()).getChildren().add(categoryTree);
                    } else {
                        TreeUtil.insertByValue(categories, categoryTree, (o1, o2) -> Integer.compare(o1.getParentId(), o2.getId()));
                    }
                    localStorage.writeObject("categories", categories);

                    Platform.runLater(() -> {
                        categoriesTreeView.getSelectionModel().getSelectedItem().setExpanded(true);
                        categoriesTreeView.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<>(categoryTree));
                        categoriesTreeView.refresh();
                    });
                }, true));
            });
        }
    }

    public void handleEditCategory() {
        if (this.categoriesTreeView.getSelectionModel().getSelectedItem() != null) {
            CategoryTree category = this.categoriesTreeView.getSelectionModel().getSelectedItem().getValue();
            FinancerTextInputDialog dialog = new FinancerTextInputDialog(I18N.get("enterCategoryName"), category.getValue().getName());
            dialog.setOnConfirm(categoryName -> {
                category.getValue().setName(categoryName);
                this.categoriesTreeView.refresh();
                this.handleUpdateCategory(category);
            });
        }
    }

    private void handleUpdateCategory(CategoryTree category) {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("category", category.getValue());

        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "updateCategory", parameters, result -> {
            categoriesTreeView.getSelectionModel().getSelectedItem().getValue().getValue().setName(category.getValue().getName());
            localStorage.writeObject("categories", categories);
        }, true));
    }

    public void handleDeleteCategory() {
        this.handleDeleteCategory(this.categoriesTreeView.getSelectionModel().getSelectedItem().getValue());
    }

    private void handleDeleteCategory(CategoryTree categoryTree) {
        if (categoryTree != null && !categoryTree.isRoot()) {
            FinancerConfirmDialog dialog = new FinancerConfirmDialog(I18N.get("confirmDeleteCategory"));
            dialog.setOnConfirm(result -> {
                Map<String, Serializable> parameters = new HashMap<>();
                parameters.put("categoryId", this.categoriesTreeView.getSelectionModel()
                        .getSelectedItem().getValue().getValue().getId());

                FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "deleteCategory", parameters, result1 -> {
                    TreeUtil.deleteByValue(categories,
                            categoriesTreeView.getSelectionModel().getSelectedItem().getValue(), Comparator.comparingInt(Category::getId));
                    localStorage.writeObject("categories", categories);

                    Platform.runLater(() -> categoriesTreeView.getSelectionModel().getSelectedItem().getParent().getChildren().remove(categoriesTreeView.getSelectionModel().getSelectedItem()));
                }, true));
            });
        }
    }

    private void createTreeView() {
        this.treeStructure = new TreeItem<>(new CategoryTreeImpl(null, new Category("root")));
        for (BaseCategory.CategoryClass categoryClass : BaseCategory.CategoryClass.values()) {
            this.createTreeView(treeStructure, this.categories.getCategoryTreeByCategoryClass(categoryClass));
        }
    }

    private void createTreeView(TreeItem<CategoryTree> treeItem, CategoryTree root) {
        TreeItem<CategoryTree> treeRoot = new TreeItem<>(root);
        treeItem.getChildren().add(treeRoot);
        if (!root.isLeaf()) {
            for (Tree<Category> categoryTree : root.getChildren()) {
                createTreeView(treeRoot, (CategoryTree) categoryTree);
            }
        }
    }

    private TextFieldTreeCellImpl getCellFactory() {
        return new TextFieldTreeCellImpl(new StringConverter<>() {
            @Override
            public String toString(CategoryTree object) {
                return object.getValue().getName().equals(object.getValue().getCategoryClass().getName()) ? I18N.get(object.getValue().getName()) : object.getValue().getName();
            }

            @Override
            public CategoryTree fromString(String string) {
                return new CategoryTreeImpl(null, new Category(string));
            }
        });
    }

    private void expandTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    private final class TextFieldTreeCellImpl extends TextFieldTreeCell<CategoryTree> {
        private ContextMenu contextMenu = new ContextMenu();
        private ContextMenu deleteContextMenu = new ContextMenu();

        TextFieldTreeCellImpl(StringConverter<CategoryTree> stringConverter) {
            super(stringConverter);

            MenuItem addMenuItem = new MenuItem(I18N.get("new"));
            addMenuItem.setOnAction(t -> handleNewCategory());
            this.contextMenu.getItems().add(addMenuItem);

            MenuItem addMenuItemDelete = new MenuItem(I18N.get("new"));
            addMenuItemDelete.setOnAction(t -> getTreeItem().getChildren().add(new TreeItem<>(new CategoryTreeImpl(null, new Category("newCategory")))));
            this.deleteContextMenu.getItems().add(addMenuItemDelete);

            MenuItem deleteMenuItem = new MenuItem(I18N.get("delete"));
            this.deleteContextMenu.getItems().add(deleteMenuItem);
            deleteMenuItem.setOnAction(t -> handleDeleteCategory(getTreeItem().getValue()));
        }

        @Override
        public void updateItem(CategoryTree item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !isEditing() && getParent() != null) {
                if (item.isRoot() && !new JavaFXFormatter(localStorage).formatCategoryName(item.getValue()).equals(I18N.get("categories"))) {
                    setContextMenu(this.contextMenu);
                } else if (!item.isRoot()) {
                    setContextMenu(this.deleteContextMenu);
                }
            }
        }
    }
}

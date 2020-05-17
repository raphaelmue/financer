package org.financer.client.javafx.main.profile;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.financer.client.domain.api.RestApi;
import org.financer.client.domain.api.RestApiImpl;
import org.financer.client.domain.model.category.Category;
import org.financer.client.domain.model.category.CategoryRoot;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.Formatter;
import org.financer.client.format.FormatterImpl;
import org.financer.client.format.I18N;
import org.financer.client.javafx.dialogs.FinancerConfirmDialog;
import org.financer.client.javafx.dialogs.FinancerTextInputDialog;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.javafx.main.FinancerController;
import org.financer.client.local.Application;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.value.objects.CategoryClass;
import org.financer.util.collections.TreeUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
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
    public TreeView<Category> categoriesTreeView;
    @FXML
    public JFXButton refreshCategoriesBtn;
    @FXML
    public JFXButton newCategoryBtn;
    @FXML
    public JFXButton editCategoryBtn;
    @FXML
    public JFXButton deleteCategoryBtn;
    @FXML
    public JFXButton editPersonalInformationBtn;

    private final RestApi restApi = new RestApiImpl();
    private final LocalStorage localStorage = LocalStorageImpl.getInstance();
    private final Formatter formatter = new FormatterImpl(localStorage);

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private CategoryRoot categoryRoot;
    private TreeItem<Category> treeStructure;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.setInitializationThread(new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            this.user = this.localStorage.readObject("user");
            if (user != null) {
                Platform.runLater(this::fillPersonalInformationLabels);
            }

            this.changePasswordLink.setOnAction(event -> {
                ChangePasswordDialog dialog = new ChangePasswordDialog(user);
                dialog.setOnConfirm(updatedUser -> restApi.updateUsersPassword(user.getId(), updatedUser.getPassword(), result ->
                        FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succChangedPassword"))).execute());
            });

            restApi.updateUsersPersonalInformation(user, result -> {
                FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succChangedPersonalInformation"));
                localStorage.writeObject("user", user);
            }).execute();

            GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
            this.refreshCategoriesBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.REFRESH));
            this.refreshCategoriesBtn.setGraphicTextGap(8);
            this.newCategoryBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.PLUS));
            this.newCategoryBtn.setGraphicTextGap(8);
            this.newCategoryBtn.setDisable(true);
            this.editCategoryBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.EDIT));
            this.editCategoryBtn.setGraphicTextGap(8);
            this.editCategoryBtn.setDisable(true);
            this.deleteCategoryBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH));
            this.deleteCategoryBtn.setGraphicTextGap(8);
            this.deleteCategoryBtn.setDisable(true);

            this.handleRefreshCategories();

            this.editPersonalInformationBtn.setOnAction(event -> {
                ChangePersonalInformationDialog dialog = new ChangePersonalInformationDialog(this.user);
                dialog.setOnConfirm(user -> {
                    if (user != null) {
                        restApi.updateUsersPersonalInformation(user, result -> {
                            FinancerController.getInstance().showToast(Application.MessageType.SUCCESS, I18N.get("succChangedPersonalInformation"));
                            localStorage.writeObject("user", user);
                            Platform.runLater(this::fillPersonalInformationLabels);
                        }).execute();
                    }
                });
            });
            this.newCategoryBtn.setDisable(true);
            this.editCategoryBtn.setDisable(true);
            this.deleteCategoryBtn.setDisable(true);

            categoryRoot = localStorage.readObject("categories");
            this.loadCategoryData();
            FinancerController.getInstance().hideLoadingBox();
        }));
        FinancerController.getInitializationThread().start();
    }

    private void fillPersonalInformationLabels() {
        this.fullNameLabel.setText(formatter.format(user.getName()));
        this.emailLabel.setText(user.getEmail().getEmailAddress());
        this.birthDateLabel.setText(formatter.format(user.getBirthDate()));
        this.genderLabel.setText(I18N.get(this.user.getGender().getGender().getName()));
    }

    public void handleRefreshCategories() {
        restApi.getUsersCategories(user.getId(), result -> {
            categoryRoot = new CategoryRoot(result);
            categoryRoot = localStorage.readObject("categories");
            Platform.runLater(this::loadCategoryData);
        }).execute();
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
        Platform.runLater(() -> categoriesTreeView.setRoot(treeStructure));
        expandTreeView(treeStructure);
        categoriesTreeView.setCellFactory(param -> getCellFactory());
        categoriesTreeView.setOnEditCommit(event -> {
            event.getNewValue().setId(event.getOldValue().getId());
            event.getNewValue().setParent(event.getOldValue().getParent());
            event.getNewValue().setCategoryClass(event.getOldValue().getCategoryClass());
            handleUpdateCategory(event.getNewValue());
        });
    }

    private void initializePersonalInformation() {
        this.fullNameLabel.setText(formatter.format(user.getName()));
        this.emailLabel.setText(user.getEmail().getEmailAddress());
        this.birthDateLabel.setText(formatter.format(user.getBirthDate()));
        this.genderLabel.setText(I18N.get(this.user.getGender().getGender().getName()));
    }

    public void handleNewCategory() {
        this.handleNewCategory(this.categoriesTreeView.getSelectionModel().getSelectedItem());
    }

    private void handleNewCategory(TreeItem<Category> currentItem) {
        if (currentItem != null) {
            FinancerTextInputDialog dialog = new FinancerTextInputDialog(I18N.get("enterCategoryName"), I18N.get("newCategory"));
            dialog.setOnConfirm(categoryName -> {
                Category newCategory = new Category()
                        .setParent(currentItem.getValue())
                        .setCategoryClass(currentItem.getValue().getCategoryClass())
                        .setName(categoryName);

                restApi.createCategory(newCategory, result -> {
                    currentItem.getChildren().add(new TreeItem<>(result));

                    if (categoriesTreeView.getSelectionModel().getSelectedItem().getValue().isRoot()) {
                        categoryRoot.getCategoriesByClass(newCategory.getCategoryClass()).add(newCategory);
                    } else {
                        TreeUtil.insertByValue(categoryRoot, newCategory, (o1, o2) -> Long.compare(((Category) o1).getParent().getId(), ((Category) o2).getId()));
                    }
                    localStorage.writeObject("categories", categoryRoot);

                    Platform.runLater(() -> {
                        categoriesTreeView.getSelectionModel().getSelectedItem().setExpanded(true);
                        categoriesTreeView.refresh();
                    });
                }).execute();
            });
        }
    }

    public void handleEditCategory() {
        if (this.categoriesTreeView.getSelectionModel().getSelectedItem() != null) {
            Category category = this.categoriesTreeView.getSelectionModel().getSelectedItem().getValue();
            FinancerTextInputDialog dialog = new FinancerTextInputDialog(I18N.get("enterCategoryName"), category.getName());
            dialog.setOnConfirm(categoryName -> {
                category.setName(categoryName);
                this.categoriesTreeView.refresh();
                this.handleUpdateCategory(category);
            });
        }
    }

    private void handleUpdateCategory(Category category) {
        restApi.updateCategory(category, result -> {
            categoriesTreeView.getSelectionModel().getSelectedItem().getValue().setName(category.getName());
            localStorage.writeObject("categories", categoryRoot);
        }).execute();
    }

    public void handleDeleteCategory() {
        this.handleDeleteCategory(this.categoriesTreeView.getSelectionModel().getSelectedItem().getValue());
    }

    private void handleDeleteCategory(Category category) {
        if (category != null && !category.isRoot()) {
            FinancerConfirmDialog dialog = new FinancerConfirmDialog(I18N.get("confirmDeleteCategory"));
            dialog.setOnConfirm(result -> restApi.deleteCategory(category.getId(), voidResult -> {
                TreeUtil.deleteByValue(categoryRoot, categoriesTreeView.getSelectionModel().getSelectedItem().getValue());
                localStorage.writeObject("categories", categoryRoot);
                Platform.runLater(() -> categoriesTreeView.getSelectionModel().getSelectedItem().getParent().getChildren().remove(categoriesTreeView.getSelectionModel().getSelectedItem()));
            }).execute());
        }
    }

    private void createTreeView() {
        this.treeStructure = new TreeItem<>(new Category().setName("root"));
        for (CategoryClass categoryClass : CategoryClass.getAll()) {
            Set<Category> categories = this.categoryRoot.getCategoriesByClass(categoryClass);
            for (Category category : categories) {
                this.createTreeView(treeStructure, category);
            }
        }
    }

    private void createTreeView(TreeItem<Category> treeItem, Category root) {
        TreeItem<Category> treeRoot = new TreeItem<>(root);
        treeItem.getChildren().add(treeRoot);
        if (!root.isLeaf()) {
            for (Category child : root.getChildren()) {
                createTreeView(treeRoot, child);
            }
        }
    }

    private TextFieldTreeCellImpl getCellFactory() {
        return new TextFieldTreeCellImpl(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                return category.getName().equals(category.getCategoryClass().getCategoryClass().getName()) ? I18N.get(category.getName()) : category.getName();
            }

            @Override
            public Category fromString(String string) {
                return new Category().setName(string);
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

    private final class TextFieldTreeCellImpl extends TextFieldTreeCell<Category> {
        private final ContextMenu contextMenu = new ContextMenu();
        private final ContextMenu deleteContextMenu = new ContextMenu();

        TextFieldTreeCellImpl(StringConverter<Category> stringConverter) {
            super(stringConverter);

            MenuItem addMenuItem = new MenuItem(I18N.get("new"));
            addMenuItem.setOnAction(t -> handleNewCategory());
            this.contextMenu.getItems().add(addMenuItem);

            MenuItem addMenuItemDelete = new MenuItem(I18N.get("new"));
            addMenuItemDelete.setOnAction(t -> getTreeItem().getChildren().add(new TreeItem<>(new Category().setName("newCategory"))));
            this.deleteContextMenu.getItems().add(addMenuItemDelete);

            MenuItem deleteMenuItem = new MenuItem(I18N.get("delete"));
            this.deleteContextMenu.getItems().add(deleteMenuItem);
            deleteMenuItem.setOnAction(t -> handleDeleteCategory(getTreeItem().getValue()));
        }

        @Override
        public void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !isEditing() && getParent() != null) {
                if (item.isRoot() && !formatter.format(item).equals(I18N.get("categories"))) {
                    setContextMenu(this.contextMenu);
                } else if (!item.isRoot()) {
                    setContextMenu(this.deleteContextMenu);
                }
            }
        }
    }
}

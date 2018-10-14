package de.raphaelmuesseler.financer.client.javafx.main.profile;

import com.jfoenix.controls.JFXButton;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerTextInputDialog;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.util.collections.SerialTreeItem;
import de.raphaelmuesseler.financer.util.collections.Tree;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.ConnectException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileController implements Initializable {

    public Label nameLabel;
    public Label surnameLabel;
    public Label emailLabel;
    public TreeView<Category> categoriesTreeView;
    public JFXButton refreshCategoriesBtn;
    public JFXButton newCategoryBtn;
    public JFXButton editCategoryBtn;
    public JFXButton deleteCategoryBtn;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();
    private BaseCategory categories;
    private LocalStorageImpl localStorage = LocalStorageImpl.getInstance();
    private TreeItem<Category> treeStructure;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * Personal Information
         */
        this.user = this.localStorage.getLoggedInUser();
        if (user != null) {
            this.nameLabel.setText(user.getName());
            this.surnameLabel.setText(user.getSurname());
            this.emailLabel.setText(user.getEmail());
        }

        /**
         * Categories
         */

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.refreshCategoriesBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.REFRESH));
        this.refreshCategoriesBtn.setGraphicTextGap(8);
        this.newCategoryBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.PLUS));
        this.newCategoryBtn.setGraphicTextGap(8);
        this.editCategoryBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.EDIT));
        this.editCategoryBtn.setGraphicTextGap(8);
        this.deleteCategoryBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.TRASH));
        this.deleteCategoryBtn.setGraphicTextGap(8);

        this.handleRefreshCategories();
    }

    public void handleRefreshCategories() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", this.user);

        this.executor.execute(new ServerRequestHandler("getUsersCategories", parameters, new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                categories = (BaseCategory) localStorage.readObject(LocalStorageImpl.PROFILE_FILE, "categories");
                localStorage.writeObject(LocalStorageImpl.PROFILE_FILE, "categories", categories);
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof ConnectException) {
                    // TODO set offline
                } else {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
                categories = (BaseCategory) localStorage.readObject(LocalStorageImpl.PROFILE_FILE, "categories");
            }

            @Override
            public void onAfter() {
                Platform.runLater(() -> {
                    createTreeView();
                    categoriesTreeView.setEditable(true);
                    categoriesTreeView.setShowRoot(false);
                    categoriesTreeView.setRoot(treeStructure);
                    expandTreeView(treeStructure);
                    categoriesTreeView.setCellFactory(param -> getCellFactory());
                    categoriesTreeView.setOnEditCommit(event -> {
                        event.getNewValue().setId(event.getOldValue().getId());
                        event.getNewValue().setParentId(event.getOldValue().getParentId());
                        event.getNewValue().setRootId(event.getOldValue().getRootId());
                        handleUpdateCategory(event.getNewValue());
                    });
                });
            }
        }));
    }

    public void handleNewCategory() {
        this.handleNewCategory((SerialTreeItem<Category>) this.categoriesTreeView.getSelectionModel().getSelectedItem());
    }

    private void handleNewCategory(SerialTreeItem<Category> currentItem) {
        if (currentItem != null && ((currentItem.getValue().getKey() != null && !currentItem.getValue().getKey().equals("categories")) ||
                currentItem.getValue().getKey() == null)) {

            String categoryName = new FinancerTextInputDialog(I18N.get("enterCategoryName"), I18N.get("newCategory"))
                    .showAndGetResult();
            if (categoryName != null) {
                Category category = new Category(-1, (currentItem.getValue().isKey() ? -1 : currentItem.getValue().getId()),
                        (currentItem.getValue().isKey() ? currentItem.getValue().getId() : currentItem.getValue().getRootId()),
                        categoryName, false);

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("user", this.user);
                parameters.put("category", category);

                this.executor.execute(new ServerRequestHandler("addCategory", parameters, new JavaFXAsyncConnectionCall() {
                    @Override
                    public void onSuccess(ConnectionResult result) {
                        Platform.runLater(() -> {
                            category.setId(((Category) result.getResult()).getId());
                            currentItem.getChildren().add(new SerialTreeItem<>(category));
                            expandTreeView(currentItem);
                        });
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        JavaFXAsyncConnectionCall.super.onFailure(exception);
                        logger.log(Level.SEVERE, exception.getMessage(), exception);
                        handleRefreshCategories();
                    }
                }));
            }
        }
    }

    public void handleEditCategory() {
        if (this.categoriesTreeView.getSelectionModel().getSelectedItem() != null) {
            Category category = this.categoriesTreeView.getSelectionModel().getSelectedItem().getValue();
            String categoryName = new FinancerTextInputDialog(I18N.get("enterCategoryName"), Formatter.formatCategoryName(category))
                    .showAndGetResult();
            category.setName(categoryName);
            this.handleUpdateCategory(category);
        }
    }

    private void handleUpdateCategory(Category category) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", category);

        this.executor.execute(new ServerRequestHandler("updateCategory", parameters, new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
            }

            @Override
            public void onFailure(Exception exception) {
                JavaFXAsyncConnectionCall.super.onFailure(exception);
                logger.log(Level.SEVERE, exception.getMessage(), exception);
                handleRefreshCategories();
            }
        }));
    }

    public void handleDeleteCategory() {
        if (this.categoriesTreeView.getSelectionModel().getSelectedItem() != null &&
                !this.categoriesTreeView.getSelectionModel().getSelectedItem().getValue().isKey()) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("category", this.categoriesTreeView.getSelectionModel()
                    .getSelectedItem().getValue());

            this.executor.execute(new ServerRequestHandler("deleteCategory", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                }

                @Override
                public void onFailure(Exception exception) {
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                }
            }));

            this.categoriesTreeView.getSelectionModel().getSelectedItem().getParent().getChildren()
                    .remove(this.categoriesTreeView.getSelectionModel().getSelectedItem());
        }
    }

    private void createTreeView() {
        this.treeStructure = new TreeItem<>(new Category(-1, "root", -1, -1));
        for (BaseCategory.CategoryClass categoryClass : BaseCategory.CategoryClass.values()) {
            this.createTreeView(treeStructure, this.categories.getCategoryTreeByCategoryClass(categoryClass));
        }
    }

    private void createTreeView(TreeItem<Category> treeItem, CategoryTree root) {
        if (root.isLeaf()) {
            treeItem.getChildren().add(new TreeItem<>(root.getValue()));
        } else {
            for (Tree<Category> categoryTree : root.getChildren()) {
                TreeItem<Category> currentTreeItem = new TreeItem<>(categoryTree.getValue());
                treeItem.getChildren().add(currentTreeItem);
                createTreeView(currentTreeItem, (CategoryTree) categoryTree);
            }
        }
    }

    private TextFieldTreeCellImpl getCellFactory() {
        return new TextFieldTreeCellImpl(new StringConverter<Category>() {
            @Override
            public String toString(Category object) {
                return object.toString();
            }

            @Override
            public Category fromString(String string) {
                return new Category(string);
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
        private ContextMenu contextMenu = new ContextMenu();
        private ContextMenu deleteContextMenu = new ContextMenu();

        TextFieldTreeCellImpl(StringConverter<Category> stringConverter) {
            super(stringConverter);

            MenuItem addMenuItem = new MenuItem(I18N.get("new"));
            addMenuItem.setOnAction(t -> {
                handleNewCategory((SerialTreeItem<Category>) getTreeItem());
            });
            this.contextMenu.getItems().add(addMenuItem);

            MenuItem addMenuItemDelete = new MenuItem(I18N.get("new"));
            addMenuItemDelete.setOnAction(t -> {
                SerialTreeItem<Category> newCategory = new SerialTreeItem<>(new Category("newCategory", true));
                getTreeItem().getChildren().add(newCategory);
            });
            this.deleteContextMenu.getItems().add(addMenuItemDelete);

            MenuItem deleteMenuItem = new MenuItem(I18N.get("delete"));
            this.deleteContextMenu.getItems().add(deleteMenuItem);
            deleteMenuItem.setOnAction(t -> getTreeItem().getParent().getChildren().remove(getTreeItem()));
        }

        @Override
        public void updateItem(Category item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !isEditing() && getParent() != null) {
                if (item.isKey() && !Formatter.formatCategoryName(item).equals(I18N.get("categories"))) {
                    setContextMenu(this.contextMenu);
                } else if (!item.isKey()) {
                    setContextMenu(this.deleteContextMenu);
                }
            }
        }
    }
}

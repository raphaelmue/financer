package de.raphaelmuesseler.financer.client.javafx.main.profile;

import com.jfoenix.controls.JFXButton;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.connection.RetrievalServiceImpl;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerConfirmDialog;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerTextInputDialog;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.connection.AsyncCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.util.collections.Tree;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

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
    public Label birthDateLabel;
    public TreeView<CategoryTree> categoriesTreeView;
    public JFXButton refreshCategoriesBtn;
    public JFXButton newCategoryBtn;
    public JFXButton editCategoryBtn;
    public JFXButton deleteCategoryBtn;

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();
    private BaseCategory categories;
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private TreeItem<CategoryTree> treeStructure;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.user = (User) this.localStorage.readObject("user");
        if (user != null) {
            this.nameLabel.setText(user.getName());
            this.surnameLabel.setText(user.getSurname());
            this.emailLabel.setText(user.getEmail());
            this.birthDateLabel.setText(this.user.getBirthDateAsLocalDate().toString());
        }

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
        RetrievalServiceImpl.getInstance().fetchCategories(this.user, new AsyncCall<BaseCategory>() {
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
                if (categories != null) {
                    Platform.runLater(() -> {
                        createTreeView();
                        categoriesTreeView.setEditable(false);
                        categoriesTreeView.setShowRoot(false);
                        categoriesTreeView.setRoot(treeStructure);
                        expandTreeView(treeStructure);
                        categoriesTreeView.setCellFactory(param -> getCellFactory());
                        categoriesTreeView.setOnEditCommit(event -> {
                            event.getNewValue().getValue().setId(event.getOldValue().getValue().getId());
                            event.getNewValue().getValue().setParentId(event.getOldValue().getValue().getParentId());
                            event.getNewValue().getValue().setRootId(event.getOldValue().getValue().getRootId());
                            handleUpdateCategory(event.getNewValue());
                        });
                    });
                }
            }
        });
    }

    public void handleNewCategory() {
        this.handleNewCategory(this.categoriesTreeView.getSelectionModel().getSelectedItem());
    }

    private void handleNewCategory(TreeItem<CategoryTree> currentItem) {
        if (currentItem != null) {
            String categoryName = new FinancerTextInputDialog(I18N.get("enterCategoryName"), I18N.get("newCategory"))
                    .showAndGetResult();
            if (categoryName != null) {
                Category category = new Category(-1, categoryName, currentItem.getValue().getValue().getId(),
                        currentItem.getValue().getCategoryClass().getIndex());

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("user", this.user);
                parameters.put("category", category);

                this.executor.execute(new ServerRequestHandler(this.user, "addCategory", parameters, new JavaFXAsyncConnectionCall() {
                    @Override
                    public void onSuccess(ConnectionResult result) {
                        handleRefreshCategories();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        JavaFXAsyncConnectionCall.super.onFailure(exception);
                        logger.log(Level.SEVERE, exception.getMessage(), exception);
                    }
                }));
            }
        }
    }

    public void handleEditCategory() {
        if (this.categoriesTreeView.getSelectionModel().getSelectedItem() != null) {
            CategoryTree category = this.categoriesTreeView.getSelectionModel().getSelectedItem().getValue();
            String categoryName = new FinancerTextInputDialog(I18N.get("enterCategoryName"), Formatter.formatCategoryName(category.getValue()))
                    .showAndGetResult();
            category.getValue().setName(categoryName);
            this.categoriesTreeView.refresh();
            this.handleUpdateCategory(category);
        }
    }

    private void handleUpdateCategory(CategoryTree category) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", category);

        this.executor.execute(new ServerRequestHandler(this.user, "updateCategory", parameters, new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                handleRefreshCategories();
            }

            @Override
            public void onFailure(Exception exception) {
                JavaFXAsyncConnectionCall.super.onFailure(exception);
                logger.log(Level.SEVERE, exception.getMessage(), exception);
            }
        }));
    }

    public void handleDeleteCategory() {
        this.handleDeleteCategory(this.categoriesTreeView.getSelectionModel().getSelectedItem().getValue());
    }

    private void handleDeleteCategory(CategoryTree categoryTree) {
        if (categoryTree != null && !categoryTree.isRoot()) {

            if (new FinancerConfirmDialog(I18N.get("confirmDeleteCategory")).showAndGetResult()) {

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("category", this.categoriesTreeView.getSelectionModel()
                        .getSelectedItem().getValue());

                this.executor.execute(new ServerRequestHandler(this.user, "deleteCategory", parameters, new JavaFXAsyncConnectionCall() {
                    @Override
                    public void onSuccess(ConnectionResult result) {
                        handleRefreshCategories();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        JavaFXAsyncConnectionCall.super.onFailure(exception);
                        logger.log(Level.SEVERE, exception.getMessage(), exception);
                    }
                }));
            }
        }
    }

    private void createTreeView() {
        this.treeStructure = new TreeItem<>(new CategoryTree(null, new Category(-1, "root", -1, -1)));
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
        return new TextFieldTreeCellImpl(new StringConverter<CategoryTree>() {
            @Override
            public String toString(CategoryTree object) {
                return object.getValue().getName().equals(object.getCategoryClass().getName()) ? I18N.get(object.toString()) : object.toString();
            }

            @Override
            public CategoryTree fromString(String string) {
                return new CategoryTree(null, new Category(-1, string, -1, -1));
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
            addMenuItem.setOnAction(t -> handleNewCategory(getTreeItem()));
            this.contextMenu.getItems().add(addMenuItem);

            MenuItem addMenuItemDelete = new MenuItem(I18N.get("new"));
            addMenuItemDelete.setOnAction(t -> getTreeItem().getChildren().add(new TreeItem<>(new CategoryTree(null, new Category(-1, "newCategory", -1, -1)))));
            this.deleteContextMenu.getItems().add(addMenuItemDelete);

            MenuItem deleteMenuItem = new MenuItem(I18N.get("delete"));
            this.deleteContextMenu.getItems().add(deleteMenuItem);
            deleteMenuItem.setOnAction(t -> handleDeleteCategory(getTreeItem().getValue()));
        }

        @Override
        public void updateItem(CategoryTree item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !isEditing() && getParent() != null) {
                if (item.isRoot() && !Formatter.formatCategoryName(item.getValue()).equals(I18N.get("categories"))) {
                    setContextMenu(this.contextMenu);
                } else if (!item.isRoot()) {
                    setContextMenu(this.deleteContextMenu);
                }
            }
        }
    }
}

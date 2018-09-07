package de.raphaelmuesseler.financer.client.ui.main.profile;

import com.jfoenix.controls.JFXButton;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.util.SerialTreeItem;
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
import java.util.List;
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
    private SerialTreeItem<Category> structure;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * Personal Information
         */
        this.user = LocalStorage.getLoggedInUser();
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

        this.executor.execute(new ServerRequestHandler("getUsersCategories", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                structure = SerialTreeItem.fromJson(((String) result.getResult()), Category.class);
                LocalStorage.writeObject(LocalStorage.PROFILE_FILE, structure.getJson().toString());
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof ConnectException) {
                    // TODO set offline
                } else {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    Platform.runLater(() -> {
                        FinancerExceptionDialog dialog = new FinancerExceptionDialog("Login", exception);
                        dialog.showAndWait();
                    });
                }
                List<Object> result = LocalStorage.readObject(LocalStorage.PROFILE_FILE);
                if (result != null && result.size() > 0) {
                    structure = SerialTreeItem.fromJson(((String) result.get(0)), Category.class);
                }
            }

            @Override
            public void onAfter() {
                Platform.runLater(() -> {
                    categoriesTreeView.setEditable(true);
                    categoriesTreeView.setRoot(structure);
                    expandTreeView(structure);
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
            Category category = new Category(-1, (currentItem.getValue().isKey() ? -1 : currentItem.getValue().getId()),
                    (currentItem.getValue().isKey() ? currentItem.getValue().getId() : currentItem.getValue().getRootId()),
                    I18N.get("newCategory"), false);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("user", this.user);
            parameters.put("category", category);

            this.executor.execute(new ServerRequestHandler("addCategory", parameters, new AsyncConnectionCall() {
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
                    AsyncConnectionCall.super.onFailure(exception);
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    handleRefreshCategories();
                }
            }));
        }
    }

    public void handleEditCategory() {
        // TODO set selected item editable
    }

    private void handleUpdateCategory(Category category) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("category", category);

        this.executor.execute(new ServerRequestHandler("updateCategory", parameters, new AsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) { }

            @Override
            public void onFailure(Exception exception) {
                AsyncConnectionCall.super.onFailure(exception);
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

            this.executor.execute(new ServerRequestHandler("deleteCategory", parameters, new AsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                }

                @Override
                public void onFailure(Exception exception) {
                    AsyncConnectionCall.super.onFailure(exception);
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                }
            }));

            this.categoriesTreeView.getSelectionModel().getSelectedItem().getParent().getChildren()
                    .remove(this.categoriesTreeView.getSelectionModel().getSelectedItem());
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
                if (item.isKey() && !item.getName().equals(I18N.get("categories"))) {
                    setContextMenu(this.contextMenu);
                } else if (!item.isKey()) {
                    setContextMenu(this.deleteContextMenu);
                }
            }
        }
    }
}

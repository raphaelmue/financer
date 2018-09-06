package de.raphaelmuesseler.financer.client.ui.main.profile;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;

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

    private User user;
    private Logger logger = Logger.getLogger("FinancerApplication");
    private ExecutorService executor = Executors.newCachedThreadPool();
    private SerialTreeItem<Category> structure;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.user = LocalStorage.getLoggedInUser();
        if (user != null) {
            this.nameLabel.setText(user.getName());
            this.surnameLabel.setText(user.getSurname());
            this.emailLabel.setText(user.getEmail());
        }

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
                    categoriesTreeView.setCellFactory(param -> new TextFieldTreeCellImpl(new StringConverter<Category>() {
                        @Override
                        public String toString(Category object) {
                            return object.toString();
                        }

                        @Override
                        public Category fromString(String string) {
                            return new Category(string);
                        }
                    }));
                });
            }
        }));
    }

    public void handleNewCategory(ActionEvent actionEvent) {

    }

    public void handleEditCategory(ActionEvent actionEvent) {

    }

    public void handleDeleteCategory(ActionEvent actionEvent) {

    }

    private void expandTreeView(TreeItem<?> item){
        if(item != null && !item.isLeaf()){
            item.setExpanded(true);
            for(TreeItem<?> child:item.getChildren()){
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
                SerialTreeItem<Category> newCategory = new SerialTreeItem<>(new Category("newCategory", true));
                getTreeItem().getChildren().add(newCategory);
                expandTreeView(getTreeItem());
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

package de.raphaelmuesseler.financer.client.ui.main.profile;

import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.URL;
import java.util.*;
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
    private TreeItem<Category> structure;


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
            public void onSuccess(ConnectionResult result)  {
                JSONObject jsonStructure = new JSONObject((String) result.getResult());
                structure = parseJsonToTreeItem(jsonStructure);
                LocalStorage.writeObject(LocalStorage.PROFILE_FILE, (String) result.getResult());
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof ConnectException) {
                    List<Object> result = LocalStorage.readObject(LocalStorage.PROFILE_FILE);
                    if (result != null && result.size() > 0) {
                        structure = parseJsonToTreeItem(new JSONObject((String) result.get(0)));
                    }
                } else {
                    logger.log(Level.SEVERE, exception.getMessage(), exception);
                    Platform.runLater(() -> {
                        FinancerExceptionDialog dialog = new FinancerExceptionDialog("Login", exception);
                        dialog.showAndWait();
                    });
                }
            }

            @Override
            public void onAfter() {
                Platform.runLater(() -> categoriesTreeView.setRoot(structure));
            }
        }));
    }

    private TreeItem<Category> parseJsonToTreeItem(JSONObject jsonObject) {
        Iterator<?> keys = jsonObject.keys();
        TreeItem<Category> result = new TreeItem<>(new Category(I18N.get("category")));
        int counter = 0;
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (jsonObject.get(key) instanceof JSONObject) {
                result.getChildren().add(parseJsonToTreeItem(jsonObject.getJSONObject(key), new TreeItem<>(new Category(Category.CATEGORIES[counter], true))));
            }
            counter++;
        }
        return result;
    }

    private TreeItem<Category> parseJsonToTreeItem(JSONObject jsonObject, TreeItem<Category> rootKey) {
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (jsonObject.get(key) instanceof JSONObject) {
                rootKey.getChildren().add(this.parseJsonToTreeItem(jsonObject.getJSONObject(key), new TreeItem<>(new Category(key))));
            }
        }
        return rootKey;
    }

    public void handleNewCategory(ActionEvent actionEvent) {

    }

    public void handleEditCategory(ActionEvent actionEvent) {

    }

    public void handleDeleteCategory(ActionEvent actionEvent) {

    }
}

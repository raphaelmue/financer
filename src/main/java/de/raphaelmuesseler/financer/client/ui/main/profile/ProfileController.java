package de.raphaelmuesseler.financer.client.ui.main.profile;

import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerExceptionDialog;
import de.raphaelmuesseler.financer.shared.connection.AsyncConnectionCall;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.util.SerialTreeItem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import org.json.JSONObject;

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
                Platform.runLater(() -> categoriesTreeView.setRoot(structure));
            }
        }));
    }

    public void handleNewCategory(ActionEvent actionEvent) {

    }

    public void handleEditCategory(ActionEvent actionEvent) {

    }

    public void handleDeleteCategory(ActionEvent actionEvent) {

    }
}

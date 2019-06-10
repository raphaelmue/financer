package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import de.raphaelmuesseler.financer.client.local.Application;
import de.raphaelmuesseler.financer.shared.model.user.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinancerController implements Initializable, Application {

    @FXML
    public BorderPane rootLayout;
    @FXML
    public BorderPane header;
    private static VBox loadingBox;
    @FXML
    public Button overviewTabBtn;
    @FXML
    public Button transactionsTabBtn;
    @FXML
    public Button statisticsTabBtn;
    @FXML
    public Button profileTabBtn;
    @FXML
    public Button settingTabBtn;
    @FXML
    public Label userNameLabel;
    @FXML
    public MenuButton accountMenuBtn;
    @FXML
    public MenuItem logoutBtn;
    @FXML
    public JFXHamburger hamburgerBtn;
    @FXML
    public Label contentLabel;
    @FXML
    public Label offlineLabel;
    public VBox navigationBox;

    private static Application instance;

    private static final Logger logger = Logger.getLogger("FinancerApplication");


    private ResourceBundle resourceBundle;
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private JFXSnackbar snackbar;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        ServerRequestHandler.setApplication(this);
        ServerRequestHandler.setLocalStorage(this.localStorage);

        try {
            ServerRequestHandler.makeRequests(Executors.newCachedThreadPool());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        User user = (User) this.localStorage.readObject("user");

        // setting up language
        this.resourceBundle = ResourceBundle.getBundle("Financer", user.getSettings().getLanguage());

        this.snackbar = new JFXSnackbar(this.rootLayout);

        try {
            loadingBox = FXMLLoader.load(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/loading.fxml"), this.resourceBundle);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        this.userNameLabel.setText(user.getFullName());

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.overviewTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.COLUMNS));
        this.overviewTabBtn.setGraphicTextGap(10);
        this.transactionsTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.MONEY));
        this.transactionsTabBtn.setGraphicTextGap(8);
        this.statisticsTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.LINE_CHART));
        this.statisticsTabBtn.setGraphicTextGap(8);
        this.profileTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.USER));
        this.profileTabBtn.setGraphicTextGap(15);
        this.settingTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.COGS));
        this.settingTabBtn.setGraphicTextGap(8);
        this.accountMenuBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.USER));
        this.accountMenuBtn.setGraphicTextGap(10);
        this.logoutBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.SIGN_OUT));

        HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(this.hamburgerBtn);
        burgerTask.setRate(1);
        this.hamburgerBtn.setAnimation(burgerTask);
        this.hamburgerBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            burgerTask.setRate(burgerTask.getRate() * -1);
            burgerTask.play();
        });

        handleShowOverviewContent();
    }

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void showLoadingBox() {
        if (loadingBox != null) {
            loadingBox.setVisible(true);
        }
    }

    @Override
    public void hideLoadingBox() {
        if (loadingBox != null && loadingBox.isVisible()) {
            loadingBox.setVisible(false);
        }
    }

    @Override
    public void setOffline() {
        this.header.setStyle("-fx-background-color: #909ca8");
        this.offlineLabel.setVisible(true);
    }

    @Override
    public void setOnline() {
        this.header.setStyle("-fx-background-color: #92cab1");
        this.offlineLabel.setVisible(false);
    }

    @Override
    public synchronized void showToast(MessageType messageType, String message) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add(messageType.getName() + "-toast-label");
        this.snackbar.enqueue(new JFXSnackbar.SnackbarEvent(messageLabel));
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void handleShowOverviewContent() {
        this.loadFXML(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/overview.fxml"));
        this.removeSelectedStyleClass();
        this.overviewTabBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("overview"));
    }


    public void handleShowTransactionsContent() {
        this.loadFXML(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/transactions.fxml"));
        this.removeSelectedStyleClass();
        this.transactionsTabBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("transactions"));
    }

    public void handleShowStatisticsContent() {
        this.loadFXML(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/statistics.fxml"));
        this.removeSelectedStyleClass();
        this.statisticsTabBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("statistics"));
    }

    public void handleShowProfileContent() {
        this.loadFXML(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/profile.fxml"));
        this.removeSelectedStyleClass();
        this.profileTabBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("profile"));
    }

    public void handleShowSettingsContent() {
        this.loadFXML(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/settings.fxml"));
        this.removeSelectedStyleClass();
        this.settingTabBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("settings"));
    }

    private void removeSelectedStyleClass() {
        final String navBtnClass = "nav-btn";
        this.overviewTabBtn.getStyleClass().clear();
        this.overviewTabBtn.getStyleClass().add(navBtnClass);
        this.transactionsTabBtn.getStyleClass().clear();
        this.transactionsTabBtn.getStyleClass().add(navBtnClass);
        this.statisticsTabBtn.getStyleClass().clear();
        this.statisticsTabBtn.getStyleClass().add(navBtnClass);
        this.profileTabBtn.getStyleClass().clear();
        this.profileTabBtn.getStyleClass().add(navBtnClass);
        this.settingTabBtn.getStyleClass().clear();
        this.settingTabBtn.getStyleClass().add(navBtnClass);
    }

    private void loadFXML(URL url) {
        try {
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(FXMLLoader.load(url, this.resourceBundle));
            stackPane.getChildren().add(loadingBox);
            this.rootLayout.setCenter(stackPane);
            BorderPane.setMargin(stackPane, new Insets(20));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void handleLogoutBtn() {
        handleLogout();
    }

    public static void handleLogout() {
        Stage stage = (Stage) loadingBox.getScene().getWindow();
        stage.close();

        LocalStorageImpl.getInstance().deleteAllData();

        try {
            new LoginApplication().start(new Stage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void onToggleNavigationBar() {
        navigationBox.setManaged(!navigationBox.isManaged());
        navigationBox.setVisible(!navigationBox.isVisible());
    }
}

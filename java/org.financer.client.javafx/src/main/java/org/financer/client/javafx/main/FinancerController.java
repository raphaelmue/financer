package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
import javafx.util.Duration;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.financer.client.connection.ServerRequest;
import org.financer.client.format.I18N;
import org.financer.client.javafx.dialogs.FinancerExceptionDialog;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.local.Application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    public JFXButton overviewTabIconBtn;
    @FXML
    public JFXButton transactionsTabBtn;
    @FXML
    public JFXButton transactionsTabIconBtn;
    @FXML
    public JFXButton statisticsTabBtn;
    @FXML
    public JFXButton statisticsTabIconBtn;
    @FXML
    public JFXButton profileTabBtn;
    @FXML
    public JFXButton profileTabIconBtn;
    @FXML
    public JFXButton settingTabBtn;
    @FXML
    public JFXButton settingTabIconBtn;
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
    @FXML
    public VBox navigationBox;
    @FXML
    public BorderPane contentPane;

    private static Application instance;

    private static Thread initializationThread = null;

    private static final Logger logger = Logger.getLogger("FinancerApplication");


    private ResourceBundle resourceBundle;
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();
    private JFXSnackbar snackbar;
    private boolean isNavigationBarHidden = false;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        ServerRequest.setApplication(this);
        ServerRequest.setLocalStorage(this.localStorage);

        try {
            ServerRequest.makeRequests(Executors.newCachedThreadPool());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        User user = (User) this.localStorage.readObject("user");

        // setting up language
        this.resourceBundle = ResourceBundle.getBundle("Financer", user.getSettings().getLanguage());

        this.snackbar = new JFXSnackbar(this.rootLayout);

        try {
            loadingBox = FXMLLoader.load(getClass().getResource("/org/financer/client/javafx/main/views/loading.fxml"), this.resourceBundle);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        this.accountMenuBtn.setText(user.getFullName());

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.accountMenuBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.USER));
        this.accountMenuBtn.setGraphicTextGap(10);
        this.logoutBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.SIGN_OUT));

        HamburgerSlideCloseTransition burgerTask = new HamburgerSlideCloseTransition(this.hamburgerBtn);
        burgerTask.setRate(1);
        Platform.runLater(() -> burgerTask.play());
        this.hamburgerBtn.setAnimation(burgerTask);
        this.hamburgerBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
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
//        Platform.runLater(() -> {
//            this.header.setStyle("-fx-background-color: #909ca8");
//            this.offlineLabel.setVisible(true);
//        });
    }

    @Override
    public void setOnline() {
//        Platform.runLater(() -> {
//            this.header.setStyle("-fx-background-color: #44a1a0");
//            this.offlineLabel.setVisible(false);
//        });
    }

    @Override
    public synchronized void showToast(MessageType messageType, String message) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add(messageType.getName() + "-toast-label");
        this.snackbar.enqueue(new JFXSnackbar.SnackbarEvent(messageLabel));
    }

    @Override
    public void showErrorDialog(Exception exception) {
        Platform.runLater(() -> new FinancerExceptionDialog("Financer", exception));
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void handleShowOverviewContent() {
        this.loadFXML(getClass().getResource("/org/financer/client/javafx/main/views/overview.fxml"));
        this.removeSelectedStyleClass();
        this.overviewTabBtn.getStyleClass().add("selected");
        this.overviewTabIconBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("overview"));
    }


    public void handleShowTransactionsContent() {
        this.loadFXML(getClass().getResource("/org/financer/client/javafx/main/views/transactions.fxml"));
        this.removeSelectedStyleClass();
        this.transactionsTabBtn.getStyleClass().add("selected");
        this.transactionsTabIconBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("transactions"));
    }

    public void handleShowStatisticsContent() {
        this.loadFXML(getClass().getResource("/org/financer/client/javafx/main/views/statistics.fxml"));
        this.removeSelectedStyleClass();
        this.statisticsTabBtn.getStyleClass().add("selected");
        this.statisticsTabIconBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("statistics"));
    }

    public void handleShowProfileContent() {
        this.loadFXML(getClass().getResource("/org/financer/client/javafx/main/views/profile.fxml"));
        this.removeSelectedStyleClass();
        this.profileTabBtn.getStyleClass().add("selected");
        this.profileTabIconBtn.getStyleClass().add("selected");
        this.contentLabel.setText(I18N.get("profile"));
    }

    public void handleShowSettingsContent() {
        this.loadFXML(getClass().getResource("/org/financer/client/javafx/main/views/settings.fxml"));
        this.removeSelectedStyleClass();
        this.settingTabBtn.getStyleClass().add("selected");
        this.settingTabIconBtn.getStyleClass().add("selected");
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

        this.overviewTabIconBtn.getStyleClass().clear();
        this.overviewTabIconBtn.getStyleClass().add(navBtnClass);
        this.transactionsTabIconBtn.getStyleClass().clear();
        this.transactionsTabIconBtn.getStyleClass().add(navBtnClass);
        this.statisticsTabIconBtn.getStyleClass().clear();
        this.statisticsTabIconBtn.getStyleClass().add(navBtnClass);
        this.profileTabIconBtn.getStyleClass().clear();
        this.profileTabIconBtn.getStyleClass().add(navBtnClass);
        this.settingTabIconBtn.getStyleClass().clear();
        this.settingTabIconBtn.getStyleClass().add(navBtnClass);
    }

    private void loadFXML(URL url) {
        try {
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(FXMLLoader.load(url, this.resourceBundle));
            stackPane.getChildren().add(loadingBox);
            this.contentPane.setCenter(stackPane);
            BorderPane.setMargin(stackPane, new Insets(20, 0, 0, 20));
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
            new FinancerApplication().start(new Stage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void onToggleNavigationBar() {
        this.overviewTabIconBtn.setVisible(!this.isNavigationBarHidden);
        this.transactionsTabIconBtn.setVisible(!this.isNavigationBarHidden);
        this.statisticsTabIconBtn.setVisible(!this.isNavigationBarHidden);
        this.profileTabIconBtn.setVisible(!this.isNavigationBarHidden);
        this.settingTabIconBtn.setVisible(!this.isNavigationBarHidden);

        Duration duration = new Duration(350);
        TranslateTransition t1 = new TranslateTransition(duration, this.navigationBox);
        TranslateTransition t2 = new TranslateTransition(duration, this.contentPane);

        List<TranslateTransition> tabIconBtnTransitions = new ArrayList<>();
        tabIconBtnTransitions.add(new TranslateTransition(duration, this.overviewTabIconBtn));
        tabIconBtnTransitions.add(new TranslateTransition(duration, this.transactionsTabIconBtn));
        tabIconBtnTransitions.add(new TranslateTransition(duration, this.statisticsTabIconBtn));
        tabIconBtnTransitions.add(new TranslateTransition(duration, this.profileTabIconBtn));
        tabIconBtnTransitions.add(new TranslateTransition(duration, this.settingTabIconBtn));
        List<TranslateTransition> tabBtnTransitions = new ArrayList<>();
        tabBtnTransitions.add(new TranslateTransition(duration, this.overviewTabBtn));
        tabBtnTransitions.add(new TranslateTransition(duration, this.transactionsTabBtn));
        tabBtnTransitions.add(new TranslateTransition(duration, this.statisticsTabBtn));
        tabBtnTransitions.add(new TranslateTransition(duration, this.profileTabBtn));
        tabBtnTransitions.add(new TranslateTransition(duration, this.settingTabBtn));

        if (!this.isNavigationBarHidden) {
            t1.setToX(-180);
            t2.setToX(-90);
            tabBtnTransitions.forEach(translateTransition -> translateTransition.setToX(-180));
            tabIconBtnTransitions.forEach(translateTransition -> {
                translateTransition.setFromX(-180);
                translateTransition.setToX(180);
            });
            this.overviewTabIconBtn.toFront();
            this.transactionsTabIconBtn.toFront();
            this.statisticsTabIconBtn.toFront();
            this.profileTabIconBtn.toFront();
            this.settingTabIconBtn.toFront();
        } else {
            t1.setToX(0);
            t2.setToX(0);
            tabBtnTransitions.forEach(translateTransition -> translateTransition.setToX(0));
            tabIconBtnTransitions.forEach(translateTransition -> translateTransition.setToX(0));
            this.overviewTabIconBtn.toBack();
            this.transactionsTabIconBtn.toBack();
            this.statisticsTabIconBtn.toBack();
            this.profileTabIconBtn.toBack();
            this.settingTabIconBtn.toBack();
        }
        ParallelTransition transition = new ParallelTransition(t1, t2);
        transition.getChildren().addAll(tabBtnTransitions);
        transition.getChildren().addAll(tabIconBtnTransitions);
        transition.play();

        this.isNavigationBarHidden = !this.isNavigationBarHidden;
    }

    public static Thread getInitializationThread() {
        return initializationThread;
    }

    public static void setInitializationThread(Thread initializationThread) {
        FinancerController.initializationThread = initializationThread;
    }
}

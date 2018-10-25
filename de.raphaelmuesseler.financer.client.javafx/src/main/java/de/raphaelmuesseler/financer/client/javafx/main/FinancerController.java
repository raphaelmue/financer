package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.login.LoginApplication;
import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.shared.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.util.Locale;
import java.util.ResourceBundle;

public class FinancerController implements Initializable {
    public BorderPane rootLayout;
    private static VBox loadingBox;
    public Button overviewTabBtn;
    public Button transactionsTabBtn;
    public Button statisticsTabBtn;
    public Button profileTabBtn;
    public Button settingTabBtn;
    public Label userNameLabel;
    public MenuButton accountMenuBtn;
    public MenuItem logoutBtn;
    public JFXHamburger hamburgerBtn;
    public Label contentLabel;

    private ResourceBundle resourceBundle;
    private LocalStorageImpl localStorage = (LocalStorageImpl) LocalStorageImpl.getInstance();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {


        // setting up language
        Locale locale;
        if (this.localStorage.readObject("settings") != null) {
            locale = ((Settings) this.localStorage.readObject("settings")).getLanguage();
        } else {
            locale = Locale.ENGLISH;
        }
        this.resourceBundle = ResourceBundle.getBundle("Financer", locale);

        try {
            loadingBox = FXMLLoader.load(getClass().getResource("/de/raphaelmuesseler/financer/client/javafx/main/views/loading.fxml"), this.resourceBundle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = (User) this.localStorage.readObject("user");
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
        burgerTask.setRate(-1);
        this.hamburgerBtn.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            burgerTask.setRate(burgerTask.getRate() * -1);
            burgerTask.play();
        });
    }

    public static boolean showLoadingBox() {
        if (loadingBox != null) {
            loadingBox.setVisible(true);
            return true;
        }
        return false;
    }

    public static boolean hideLoadingBox() {
        if (loadingBox != null && loadingBox.isVisible()) {
            loadingBox.setVisible(false);
            return true;
        }
        return false;
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
        this.overviewTabBtn.getStyleClass().clear();
        this.overviewTabBtn.getStyleClass().add("nav-btn");
        this.transactionsTabBtn.getStyleClass().clear();
        this.transactionsTabBtn.getStyleClass().add("nav-btn");
        this.statisticsTabBtn.getStyleClass().clear();
        this.statisticsTabBtn.getStyleClass().add("nav-btn");
        this.profileTabBtn.getStyleClass().clear();
        this.profileTabBtn.getStyleClass().add("nav-btn");
        this.settingTabBtn.getStyleClass().clear();
        this.settingTabBtn.getStyleClass().add("nav-btn");
    }

    private void loadFXML(URL url) {
        try {
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(FXMLLoader.load(url, this.resourceBundle));
            stackPane.getChildren().add(loadingBox);
            this.rootLayout.setCenter(stackPane);
            // TODO bring center to back
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLogout() {
        Stage stage = (Stage) this.accountMenuBtn.getScene().getWindow();
        stage.close();

        this.localStorage.deleteAllData();

        try {
            new LoginApplication().start(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onToggleNavigationBar(MouseEvent mouseEvent) {
    }
}

package de.raphaelmuesseler.financer.client.ui.main;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class FinancerController implements Initializable {
    public BorderPane rootLayout;
    public Button overviewTabBtn;
    public Button statisticsTabBtn;
    public Button profileTabBtn;
    public Button settingTabBtn;
    public Button transactionsTabBtn;

    private ResourceBundle resourceBundle;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // setting up language
        Locale locale;
        if (LocalStorage.getSettings() != null) {
            locale = LocalStorage.getSettings().getLanguage();
        } else {
            locale = Locale.ENGLISH;
        }
        this.resourceBundle = ResourceBundle.getBundle("Financer", locale);
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void handleShowOverviewContent(ActionEvent actionEvent) {
        this.loadFXML(getClass().getResource("/views/overview.fxml"));
        this.removeSelectedStyleClass();
        this.overviewTabBtn.getStyleClass().add("selected");
    }


    public void handleShowTransactionsContent(ActionEvent actionEvent) {
        this.loadFXML(getClass().getResource("/views/tranactions.fxml"));
        this.removeSelectedStyleClass();
        this.transactionsTabBtn.getStyleClass().add("selected");
    }

    public void handleShowStatisticsContent(ActionEvent actionEvent) {
        this.loadFXML(getClass().getResource("/views/statistics.fxml"));
        this.removeSelectedStyleClass();
        this.statisticsTabBtn.getStyleClass().add("selected");
    }

    public void handleShowProfileContent(ActionEvent actionEvent) {
        this.loadFXML(getClass().getResource("/views/profile.fxml"));
        this.removeSelectedStyleClass();
        this.profileTabBtn.getStyleClass().add("selected");
    }

    public void handleShowSettingsContent(ActionEvent actionEvent) {
        this.loadFXML(getClass().getResource("/views/settings.fxml"));
        this.removeSelectedStyleClass();
        this.settingTabBtn.getStyleClass().add("selected");
    }

    private void removeSelectedStyleClass() {
        this.overviewTabBtn.getStyleClass().clear();
        this.overviewTabBtn.getStyleClass().add("nav-btn");
        this.statisticsTabBtn.getStyleClass().clear();
        this.statisticsTabBtn.getStyleClass().add("nav-btn");
        this.profileTabBtn.getStyleClass().clear();
        this.profileTabBtn.getStyleClass().add("nav-btn");
        this.settingTabBtn.getStyleClass().clear();
        this.settingTabBtn.getStyleClass().add("nav-btn");
    }

    private void loadFXML(URL url) {
        try {
            this.rootLayout.setCenter(FXMLLoader.load(url, this.resourceBundle));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

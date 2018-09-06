package de.raphaelmuesseler.financer.client.ui.main.settings;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.client.ui.I18N;
import de.raphaelmuesseler.financer.client.ui.dialogs.FinancerDialog;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.*;

public class SettingsController implements Initializable {
    public MenuButton languageMenuBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.languageMenuBtn.setText(I18N.get("language"));

        List<MenuItem> menuItems = new ArrayList<>();
        for (Map.Entry<String, Locale> entry : I18N.LANGUAGES.entrySet()) {
            MenuItem menuItem = new MenuItem(entry.getKey());
            menuItem.setOnAction(event -> {
                changeSettings(entry.getValue());
            });
            menuItems.add(menuItem);
        }
        this.languageMenuBtn.getItems().addAll(menuItems);
    }

    private void changeSettings(Locale locale) {
        Settings settings = LocalStorage.getSettings();
        if (settings == null) {
            settings = new Settings();
        }
        settings.setLanguage(locale);
        LocalStorage.writeSettings(settings);

        new FinancerDialog(Alert.AlertType.INFORMATION, I18N.get("language"), I18N.get("warnChangesAfterRestart")).showAndWait();
    }

}

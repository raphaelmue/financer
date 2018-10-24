package de.raphaelmuesseler.financer.client.javafx.main.settings;

import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.local.Settings;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.Currency;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public ComboBox<I18N.Language> languageMenuComboBox;
    public ComboBox<Currency> currencyComboBox;
    public CheckBox showSignCheckbox;

    private Settings settings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.settings = (Settings) LocalStorageImpl.getInstance().readObject("settings");
        if (this.settings == null) {
            this.settings = new Settings();
        }

        this.languageMenuComboBox.getItems().addAll(I18N.Language.getAll());
        this.languageMenuComboBox.getSelectionModel().select(I18N.Language.getLanguageByLocale(this.settings.getLanguage()));
        this.languageMenuComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                settings.setLanguage(newValue.getLocale());
                LocalStorageImpl.getInstance().writeObject("settings", settings);
            });

        this.currencyComboBox.getItems().addAll(Currency.getAvailableCurrencies());
        this.currencyComboBox.getItems().sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString()));
        this.currencyComboBox.getSelectionModel().select(this.settings.getCurrency());
        this.currencyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            settings.setCurrency(newValue);
            LocalStorageImpl.getInstance().writeObject("settings", settings);
        });

        this.showSignCheckbox.setSelected(this.settings.isShowCurrencySign());
        this.showSignCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            settings.setShowCurrencySign(newValue);
            LocalStorageImpl.getInstance().writeObject("settings", settings);
        });
    }
}

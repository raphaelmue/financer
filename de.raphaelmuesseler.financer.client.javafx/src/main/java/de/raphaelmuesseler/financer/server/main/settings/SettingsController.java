package de.raphaelmuesseler.financer.shared.server.main.settings;

import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.client.ui.format.I18N;
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
        this.settings = Settings.getSettings();

        this.languageMenuComboBox.getItems().addAll(I18N.Language.getAll());
        this.languageMenuComboBox.getSelectionModel().select(I18N.Language.getLanguageByLocale(this.settings.getLanguage()));
        this.languageMenuComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                settings.setLanguage(newValue.getLocale());
                settings.save();
            });

        this.currencyComboBox.getItems().addAll(Currency.getAvailableCurrencies());
        this.currencyComboBox.getItems().sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString()));
        this.currencyComboBox.getSelectionModel().select(this.settings.getCurrency());
        this.currencyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            settings.setCurrency(newValue);
            settings.save();
        });

        this.showSignCheckbox.setSelected(this.settings.isShowCurrencySign());
        this.showSignCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            settings.setShowCurrencySign(newValue);
            settings.save();
        });
    }
}

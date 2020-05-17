package org.financer.client.javafx.main.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.financer.client.domain.api.RestApi;
import org.financer.client.domain.api.RestApiImpl;
import org.financer.client.domain.model.user.Token;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.I18N;
import org.financer.client.javafx.dialogs.FinancerConfirmDialog;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.client.javafx.main.FinancerController;
import org.financer.client.javafx.util.ApplicationHelper;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.value.objects.SettingPair;

import java.net.URL;
import java.util.Currency;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    public JFXComboBox<I18N.Language> languageMenuComboBox;
    @FXML
    public JFXComboBox<Currency> currencyComboBox;
    @FXML
    public JFXToggleButton showSignCheckbox;
    @FXML
    public JFXComboBox<Integer> maxNumberOfMonthsDisplayedComboBox;
    @FXML
    public JFXButton logoutFromDeviceBtn;
    @FXML
    public JFXListView<Token> devicesListView;
    @FXML
    public JFXToggleButton changeAmountSignAutomaticallyCheckBox;

    private final RestApi restApi = new RestApiImpl();
    private final LocalStorage localStorage = LocalStorageImpl.getInstance();
    private User user = localStorage.readObject("user");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.setInitializationThread(new Thread(() -> {
            FinancerController.getInstance().showLoadingBox();
            GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
            this.logoutFromDeviceBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.SIGN_OUT));

            this.languageMenuComboBox.getItems().addAll(I18N.Language.getAll());
            this.languageMenuComboBox.getSelectionModel().select(I18N.Language.getLanguageByLocale(this.user.getValueOrDefault(SettingPair.Property.LANGUAGE)));
            this.languageMenuComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, newValue.getLocale());
                updateSettings();

                FinancerConfirmDialog dialog = new FinancerConfirmDialog(I18N.get("warnChangesAfterRestart"));
                dialog.setOnConfirm(result -> ApplicationHelper.restartApplication((Stage) languageMenuComboBox.getScene().getWindow()));
            });

            this.currencyComboBox.getItems().addAll(Currency.getAvailableCurrencies());
            this.currencyComboBox.getItems().sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString()));
            this.currencyComboBox.getSelectionModel().select(this.user.getValueOrDefault(SettingPair.Property.CURRENCY));
            this.currencyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                user.putOrUpdateSettingProperty(SettingPair.Property.CURRENCY, newValue);
                showSignCheckbox.setDisable(false);
                updateSettings();
            });

            if (!user.isPropertySet(SettingPair.Property.CURRENCY)) {
                this.showSignCheckbox.setDisable(true);
            }
            this.showSignCheckbox.setSelected(this.user.getValueOrDefault(SettingPair.Property.SHOW_CURRENCY_SIGN));
            this.showSignCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                user.putOrUpdateSettingProperty(SettingPair.Property.SHOW_CURRENCY_SIGN, newValue);
                updateSettings();
            });

            for (int i = 3; i <= 8; i++) this.maxNumberOfMonthsDisplayedComboBox.getItems().add(i);
            this.maxNumberOfMonthsDisplayedComboBox.getSelectionModel().select((user.getValueOrDefault(SettingPair.Property.MAX_NUMBER_OF_MONTHS_DISPLAYED)));
            this.maxNumberOfMonthsDisplayedComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                user.putOrUpdateSettingProperty(SettingPair.Property.MAX_NUMBER_OF_MONTHS_DISPLAYED, newValue);
                updateSettings();
            });

            this.changeAmountSignAutomaticallyCheckBox.setSelected(this.user.getValueOrDefault(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY));
            this.changeAmountSignAutomaticallyCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
                user.putOrUpdateSettingProperty(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY, newValue);
                updateSettings();
            });

            this.loadTokenListView();
            FinancerController.getInstance().hideLoadingBox();
        }));
        FinancerController.getInitializationThread().start();
    }

    private void loadTokenListView() {
        this.devicesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                logoutFromDeviceBtn.setDisable(false));

        devicesListView.setItems(FXCollections.observableArrayList(user.getTokens()));
        devicesListView.setCellFactory(param -> new TokenListViewImpl());
    }

    private void updateSettings() {
        restApi.updateUsersSettings(user.getId(), user.getSettings(), result -> {
            localStorage.writeObject("user", result);
            user = result;
        }).execute();
    }

    public void handleLogoutFromDevice() {
        FinancerConfirmDialog dialog = new FinancerConfirmDialog(I18N.get("confirmLogDeviceOut"));
        dialog.setOnConfirm(result -> restApi.deleteToken(user.getId(), this.devicesListView.getSelectionModel().getSelectedItem().getId(),
                result1 -> Platform.runLater(() -> devicesListView.getItems().remove(devicesListView.getSelectionModel().getSelectedItem()))));
    }

    private static final class TokenListViewImpl extends ListCell<Token> {
        @Override
        protected void updateItem(Token item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                BorderPane borderPane = new BorderPane();

                VBox left = new VBox();
                Label systemLabel = new Label(item.getOperatingSystem().toString());
                GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
                systemLabel.setGraphic(fontAwesome.create(item.getOperatingSystem().getOperatingSystem().getIsMobile() ? FontAwesome.Glyph.MOBILE : FontAwesome.Glyph.DESKTOP));
                systemLabel.getStyleClass().add("list-cell-title");
                left.getChildren().add(systemLabel);
                Label ipAddressLabel = new Label(item.getIpAddress().getIpAddress());
                left.getChildren().add(ipAddressLabel);
                borderPane.setLeft(left);

                borderPane.setCenter(new Label(I18N.get("lastLogin") + " " + item.getExpireDate().getExpireDate().minusMonths(1).toString()));

                setGraphic(borderPane);
            }
        }
    }
}

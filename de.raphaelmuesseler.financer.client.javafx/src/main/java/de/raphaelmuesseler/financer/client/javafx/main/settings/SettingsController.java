package de.raphaelmuesseler.financer.client.javafx.main.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerConfirmDialog;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.util.ApplicationHelper;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.user.Token;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.concurrency.FinancerExecutor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public ComboBox<I18N.Language> languageMenuComboBox;
    public ComboBox<Currency> currencyComboBox;
    public CheckBox showSignCheckbox;
    public JFXButton logoutFromDeviceBtn;
    public JFXListView<Token> devicesListView;

    private LocalStorage localStorage = LocalStorageImpl.getInstance();
    private User user = (User) localStorage.readObject("user");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.logoutFromDeviceBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.SIGN_OUT));


        this.languageMenuComboBox.getItems().addAll(I18N.Language.getAll());
        this.languageMenuComboBox.getSelectionModel().select(I18N.Language.getLanguageByLocale(this.user.getSettings().getLanguage()));
        this.languageMenuComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            user.getSettings().setLanguage(newValue.getLocale());
            updateSettings();

            if (new FinancerConfirmDialog(I18N.get("warnChangesAfterRestart")).showAndGetResult()) {
                ApplicationHelper.restartApplication((Stage) languageMenuComboBox.getScene().getWindow());
            }
        });

        this.currencyComboBox.getItems().addAll(Currency.getAvailableCurrencies());
        this.currencyComboBox.getItems().sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString()));
        this.currencyComboBox.getSelectionModel().select(this.user.getSettings().getCurrency());
        this.currencyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            user.getSettings().setCurrency(newValue);
            showSignCheckbox.setDisable(false);
            updateSettings();
        });

        if (user.getSettings().getCurrency() == null) {
            this.showSignCheckbox.setDisable(true);
        }
        this.showSignCheckbox.setSelected(this.user.getSettings().isShowCurrencySign());
        this.showSignCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            user.getSettings().setShowCurrencySign(newValue);
            updateSettings();
        });

        this.loadTokenListView();
    }

    private void loadTokenListView() {
        this.devicesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                logoutFromDeviceBtn.setDisable(false));

        devicesListView.setItems(FXCollections.observableArrayList(user.getTokenList()));
        devicesListView.setCellFactory(param -> new TokenListViewImpl());
    }

    private void updateSettings() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        FinancerExecutor.getExecutor().execute(new ServerRequestHandler(user, "updateUsersSettings", parameters, new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
            }

            @Override
            public void onFailure(Exception exception) {
                JavaFXAsyncConnectionCall.super.onFailure(exception);
            }

            @Override
            public void onAfter() {
                localStorage.writeObject("user", user);
            }
        }));
    }

    public void handleLogoutFromDevice() {
        if (new FinancerConfirmDialog(I18N.get("confirmLogDeviceOut")).showAndGetResult()) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("tokenId", this.devicesListView.getSelectionModel().getSelectedItem().getId());
            FinancerExecutor.getExecutor().execute(new ServerRequestHandler(this.user, "deleteToken", parameters, new JavaFXAsyncConnectionCall() {
                @Override
                public void onSuccess(ConnectionResult result) {
                    Platform.runLater(() -> devicesListView.getItems().remove(devicesListView.getSelectionModel().getSelectedItem()));
                }

                @Override
                public void onFailure(Exception exception) {
                    JavaFXAsyncConnectionCall.super.onFailure(exception);
                }
            }));
        }
    }

    private final class TokenListViewImpl extends ListCell<Token> {
        @Override
        protected void updateItem(Token item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                BorderPane borderPane = new BorderPane();

                VBox left = new VBox();
                Label systemLabel = new Label(item.getSystem());
                GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
                systemLabel.setGraphic(fontAwesome.create(item.getIsMobile() ? FontAwesome.Glyph.MOBILE : FontAwesome.Glyph.DESKTOP));
                systemLabel.getStyleClass().add("list-cell-title");
                left.getChildren().add(systemLabel);
                Label ipAddressLabel = new Label(item.getIpAddress());
                left.getChildren().add(ipAddressLabel);
                borderPane.setLeft(left);

                borderPane.setCenter(new Label(I18N.get("lastLogin") + " " + item.getExpireDate().minusMonths(1).toString()));

                setGraphic(borderPane);
            }
        }
    }
}

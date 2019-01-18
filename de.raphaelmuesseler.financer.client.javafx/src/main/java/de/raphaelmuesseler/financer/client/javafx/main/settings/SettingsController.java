package de.raphaelmuesseler.financer.client.javafx.main.settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import de.raphaelmuesseler.financer.client.connection.ServerRequestHandler;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.connection.JavaFXAsyncConnectionCall;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerConfirmDialog;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.client.javafx.main.FinancerController;
import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.shared.connection.ConnectionResult;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.shared.model.db.Token;
import de.raphaelmuesseler.financer.util.collections.CollectionUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsController implements Initializable {
    public ComboBox<I18N.Language> languageMenuComboBox;
    public ComboBox<Currency> currencyComboBox;
    public CheckBox showSignCheckbox;
    public JFXButton logoutFromDeviceBtn;
    public JFXListView<Token> devicesListView;

    private Settings settings;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private User user = (User) LocalStorageImpl.getInstance().readObject("user");
    private ObservableList<Token> tokens;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FinancerController.showLoadingBox();

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        this.logoutFromDeviceBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.SIGN_OUT));


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

        this.loadTokenListView();

        FinancerController.hideLoadingBox();
    }

    private void loadTokenListView() {
        this.devicesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                logoutFromDeviceBtn.setDisable(false));

        HashMap<String, Object> paramters = new HashMap<>();
        paramters.put("user", this.user);
        this.executor.execute(new ServerRequestHandler(this.user, "getUsersTokens", paramters, new JavaFXAsyncConnectionCall() {
            @Override
            public void onSuccess(ConnectionResult result) {
                tokens = CollectionUtil.castObjectListToObservable((List<Object>) result.getResult());

                Platform.runLater(() -> {
                    devicesListView.setItems(tokens);
                    devicesListView.setCellFactory(param -> new TokenListViewImpl());
                });
            }

            @Override
            public void onFailure(Exception exception) {
                JavaFXAsyncConnectionCall.super.onFailure(exception);
            }
        }));

    }

    public void handleLogoutFromDevice() {
        if (new FinancerConfirmDialog(I18N.get("confirmLogDeviceOut")).showAndGetResult()) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("tokenId", this.devicesListView.getSelectionModel().getSelectedItem().getId());
            this.executor.execute(new ServerRequestHandler(this.user, "deleteToken", parameters, new JavaFXAsyncConnectionCall() {
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
                systemLabel.setGraphic(fontAwesome.create(item.isMobile() ? FontAwesome.Glyph.MOBILE : FontAwesome.Glyph.DESKTOP));
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

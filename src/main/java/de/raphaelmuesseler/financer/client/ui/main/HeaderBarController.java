package de.raphaelmuesseler.financer.client.ui.main;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.ui.login.LoginApplication;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HeaderBarController implements Initializable {
    public MenuButton accountMenuBtn;
    public MenuItem logoutBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        accountMenuBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.USER));
        accountMenuBtn.setGraphicTextGap(10);

        logoutBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.SIGN_OUT));
    }

    public void handleLogout() {
        LocalStorage.logUserOut();

        Stage stage = (Stage) this.accountMenuBtn.getScene().getWindow();
        stage.close();

        try {
            new LoginApplication().start(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

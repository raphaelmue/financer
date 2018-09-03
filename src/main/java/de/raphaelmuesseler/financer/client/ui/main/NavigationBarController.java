package de.raphaelmuesseler.financer.client.ui.main;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.URL;
import java.util.ResourceBundle;

public class NavigationBarController implements Initializable {
    public VBox navigationBox;
    public Button overviewTabBtn;
    public Button statisticsTabBtn;
    public Button profileTabBtn;
    public Button settingTabBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        overviewTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.COLUMNS));
        overviewTabBtn.setGraphicTextGap(10);
        statisticsTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.LINE_CHART));
        statisticsTabBtn.setGraphicTextGap(10);
        profileTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.USERS));
        profileTabBtn.setGraphicTextGap(10);
        settingTabBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.COGS));
        settingTabBtn.setGraphicTextGap(10);
    }
}

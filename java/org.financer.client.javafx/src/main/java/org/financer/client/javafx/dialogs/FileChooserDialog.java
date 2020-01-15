package org.financer.client.javafx.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import org.financer.client.format.I18N;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.io.File;

public class FileChooserDialog extends FinancerDialog<File> {

    private JFXTextField pathTextField;

    public FileChooserDialog() {
        super(null);
    }

    @Override
    protected Region getDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);

        this.pathTextField = new JFXTextField();
        this.pathTextField.setId("pathTextField");
        this.pathTextField.setPromptText(I18N.get("pathToAttachment"));
        this.pathTextField.setLabelFloat(true);
        this.pathTextField.setPrefWidth(300);

        gridPane.add(this.pathTextField, 0, 0);

        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        JFXButton chooseFileBtn = new JFXButton(I18N.get("selectFile"));
        chooseFileBtn.setGraphic(fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN));
        chooseFileBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(I18N.get("uploadAttachment"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(I18N.get("documents"),
                    "*.jpg", "*.png", "*.doc", "*.docx", "*.pdf"));
            setValue(fileChooser.showOpenDialog(chooseFileBtn.getContextMenu()));
            this.pathTextField.setText(getValue().getAbsolutePath());
        });

        gridPane.add(chooseFileBtn, 1, 0);

        return gridPane;
    }

    @Override
    protected boolean checkConsistency() {
        boolean result = true;
        if (this.getValue() == null) {
            if (this.pathTextField.getText().isEmpty()) {
                setErrorMessage(I18N.get("errFillRequiredFields"));
                result = false;
            } else {
                this.setValue(new File(this.pathTextField.getText()));
            }
        }

        if (this.getValue() != null && !this.getValue().exists()) {
            setErrorMessage(I18N.get("errPathIsInvalid"));
            result = false;
        }
        return result;
    }

    @Override
    protected void onConfirm() {
        if (this.getValue() != null) {
            this.setValue(new File(this.pathTextField.getText()));
        }
        super.onConfirm();
    }
}

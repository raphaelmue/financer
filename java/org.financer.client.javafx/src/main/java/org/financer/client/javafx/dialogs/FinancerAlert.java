package org.financer.client.javafx.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.financer.client.format.I18N;
import org.financer.client.javafx.main.FinancerController;
import org.financer.client.local.Application;

public class FinancerAlert extends JFXDialog {

    private JFXDialogLayout dialogLayout;

    public FinancerAlert(Application.MessageType messageType, String headerMessage, String contentMessage) {
        this((StackPane) ((BorderPane) ((FinancerController) FinancerController.getInstance()).getRootLayout().getCenter()).getCenter(),
                messageType, headerMessage, contentMessage);
    }


    public FinancerAlert(StackPane container, Application.MessageType messageType, String headerMessage, String contentMessage) {
        super();

        // set container
        this.setDialogContainer(container);

        // set transition type
        this.setTransitionType(DialogTransition.CENTER);

        // set dialog content
        this.dialogLayout = new JFXDialogLayout();


        Label headingLabel = new Label(headerMessage);
        headingLabel.setGraphicTextGap(15);
        GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
        FontAwesome.Glyph glyph = null;
        switch (messageType) {
            case SUCCESS:
                glyph = FontAwesome.Glyph.CHECK;
                break;
            case INFO:
                glyph = FontAwesome.Glyph.INFO;
                break;
            case WARNING:
                glyph = FontAwesome.Glyph.WARNING;
                break;
            case ERROR:
                glyph = FontAwesome.Glyph.TIMES;
                break;
        }
        headingLabel.setGraphic(fontAwesome.create(glyph).size(30));
        headingLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: -fx-color-" + messageType.getName());
        this.dialogLayout.setHeading(headingLabel);

        Label contentLabel = new Label(contentMessage);
        contentLabel.setWrapText(true);
        this.dialogLayout.setBody(contentLabel);

        // add action buttons
        JFXButton okBtn = new JFXButton(I18N.get("ok"));
        okBtn.setOnAction(event -> close());

        this.dialogLayout.setActions(okBtn);

        this.setContent(this.dialogLayout);

        // show dialog
        this.show();
    }
}
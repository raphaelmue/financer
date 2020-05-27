package org.financer.client.javafx.dialogs

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.format.I18N.get
import org.financer.client.javafx.main.FinancerController
import org.financer.client.local.Application

open class FinancerAlert(container: StackPane?, messageType: Application.MessageType, headerMessage: String?, contentMessage: String?) : JFXDialog() {
    private val dialogLayout: JFXDialogLayout

    constructor(messageType: Application.MessageType, headerMessage: String?, contentMessage: String?) : this(((FinancerController.instance as FinancerController).rootLayout!!.center as BorderPane).center as StackPane,
            messageType, headerMessage, contentMessage) {
    }

    init {

        // set container
        dialogContainer = container

        // set transition type
        transitionType = DialogTransition.CENTER

        // set dialog content
        dialogLayout = JFXDialogLayout()
        val headingLabel = Label(headerMessage)
        headingLabel.graphicTextGap = 15.0
        val fontAwesome = GlyphFontRegistry.font("FontAwesome")
        var glyph: FontAwesome.Glyph? = null
        glyph = when (messageType) {
            Application.MessageType.SUCCESS -> FontAwesome.Glyph.CHECK
            Application.MessageType.INFO -> FontAwesome.Glyph.INFO
            Application.MessageType.WARNING -> FontAwesome.Glyph.WARNING
            Application.MessageType.ERROR -> FontAwesome.Glyph.TIMES
        }
        headingLabel.graphic = fontAwesome.create(glyph).size(30.0)
        headingLabel.style = "-fx-font-size: 18px; -fx-text-fill: -fx-color-" + messageType.type
        dialogLayout.setHeading(headingLabel)
        val contentLabel = Label(contentMessage)
        contentLabel.isWrapText = true
        dialogLayout.setBody(contentLabel)

        // add action buttons
        val okBtn = JFXButton(get("ok"))
        okBtn.onAction = EventHandler { close() }
        dialogLayout.setActions(okBtn)
        content = dialogLayout

        // show dialog
        this.show()
    }
}
package org.financer.client.javafx.dialogs

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.layout.GridPane
import javafx.scene.layout.Region
import javafx.stage.FileChooser
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.format.I18N.get
import java.io.File

class FileChooserDialog : FinancerDialog<File?>(null) {
    private var pathTextField: JFXTextField? = null
    override val dialogContent: Region?
        get() {
            val gridPane = GridPane()
            gridPane.hgap = 20.0
            pathTextField = JFXTextField()
            pathTextField!!.id = "pathTextField"
            pathTextField!!.promptText = get("pathToAttachment")
            pathTextField!!.isLabelFloat = true
            pathTextField!!.prefWidth = 300.0
            gridPane.add(pathTextField, 0, 0)
            val fontAwesome = GlyphFontRegistry.font("FontAwesome")
            val chooseFileBtn = JFXButton(get("selectFile"))
            chooseFileBtn.graphic = fontAwesome.create(FontAwesome.Glyph.FOLDER_OPEN)
            chooseFileBtn.onAction = EventHandler { event: ActionEvent? ->
                val fileChooser = FileChooser()
                fileChooser.title = get("uploadAttachment")
                fileChooser.initialDirectory = File(System.getProperty("user.home"))
                fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter(get("documents"),
                        "*.jpg", "*.png", "*.doc", "*.docx", "*.pdf"))
                value = fileChooser.showOpenDialog(chooseFileBtn.contextMenu)
                pathTextField!!.text = value!!.absolutePath
            }
            gridPane.add(chooseFileBtn, 1, 0)
            return gridPane
        }

    override fun checkConsistency(): Boolean {
        var result = true
        if (value == null) {
            if (pathTextField!!.text.isEmpty()) {
                errorMessage = get("errFillRequiredFields")
                result = false
            } else {
                value = File(pathTextField!!.text)
            }
        }
        if (value != null && !value!!.exists()) {
            errorMessage = get("errPathIsInvalid")
            result = false
        }
        return result
    }

    override fun onConfirm() {
        if (value != null) {
            value = File(pathTextField!!.text)
        }
        super.onConfirm()
    }
}
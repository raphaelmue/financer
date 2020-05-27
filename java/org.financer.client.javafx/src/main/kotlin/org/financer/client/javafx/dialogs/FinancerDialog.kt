package org.financer.client.javafx.dialogs

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import org.financer.client.format.I18N.get
import org.financer.client.javafx.main.FinancerController
import org.financer.util.collections.Action

/**
 * This class represents a Dialog, aligned with the needs of a general user interaction dialog. The design is base on
 * the JFXDialog [JFXDialog].
 *
 * @param <T> class type that is to be the return type of this dialog
</T> */
abstract class FinancerDialog<T> @JvmOverloads constructor(value: T, container: StackPane? = ((FinancerController.instance as FinancerController).rootLayout!!.center as BorderPane).center as StackPane) : JFXDialog() {
    protected var value: T
    private val errorMessageLabel: Label
    protected var errorMessage: String? = null
    private val dialogLayout: JFXDialogLayout
    private var confirmAction: Action<T>? = null
    private var cancelAction: Action<T>? = null
    protected open val dialogWidth: Double
        get() = 400.0


    init {
        this.value = value
    }

    private fun showErrorMessage() {
        errorMessageLabel.text = errorMessage
        errorMessageLabel.isManaged = true
    }

    protected fun setDialogTitle(title: String?) {
        dialogLayout.setHeading(Text(title))
    }

    protected abstract fun checkConsistency(): Boolean
    protected abstract val dialogContent: Region?
    protected open fun prepareDialogContent() {}

    protected open fun onCancel() {
        cancelAction?.action(value)
    }

    protected open fun onConfirm() {
        confirmAction?.action(value)
    }

    fun setOnConfirm(confirmAction: Action<T>?) {
        this.confirmAction = confirmAction
    }

    fun setOnCancel(cancelAction: Action<T>?) {
        this.cancelAction = cancelAction
    }

    init {
        if (value != null) {
            this.value = value
        }

        // set container
        dialogContainer = container

        // set transition type
        transitionType = DialogTransition.CENTER

        // set dialog content
        dialogLayout = JFXDialogLayout()

        // initialize error message label
        errorMessageLabel = Label()
        errorMessageLabel.style = "-fx-text-fill: #ff4a39;" +
                "    -fx-padding: 5 0 15 0;" +
                "    -fx-font-weight: 700;"
        errorMessageLabel.isManaged = false
        val dialogContent = dialogContent
        dialogLayout.setBody(VBox(errorMessageLabel, dialogContent))

        // add action buttons
        val okBtn = JFXButton(get("ok"))
        okBtn.onAction = EventHandler { event: ActionEvent? ->
            if (checkConsistency()) {
                onConfirm()
                close()
            } else {
                showErrorMessage()
            }
        }
        val cancelBtn = JFXButton(get("cancel"))
        cancelBtn.onAction = EventHandler { event: ActionEvent? ->
            close()
            onCancel()
        }
        dialogLayout.setActions(okBtn, cancelBtn)
        content = dialogLayout
        content.prefWidth = dialogWidth

        // show dialog
        this.show()
    }
}
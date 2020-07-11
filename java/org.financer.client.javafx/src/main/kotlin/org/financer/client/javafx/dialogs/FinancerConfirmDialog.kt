package org.financer.client.javafx.dialogs

import com.jfoenix.controls.JFXDialogLayout
import javafx.scene.control.Label
import javafx.scene.layout.Region

open class FinancerConfirmDialog(private val question: String) : FinancerDialog<Boolean?>(false) {
    private var questionLabel: Label? = null
    override val dialogContent: Region?
        get() {
            questionLabel = Label()
            return questionLabel
        }

    override fun prepareDialogContent() {
        questionLabel!!.text = question
        (content as JFXDialogLayout).actions[0].requestFocus()
    }

    override fun checkConsistency(): Boolean {
        return true
    }

    override fun onConfirm() {
        value = true
        super.onConfirm()
    }

    override fun onCancel() {
        value = false
        super.onCancel()
    }

    init {
        prepareDialogContent()
    }
}
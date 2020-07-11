package org.financer.client.javafx.dialogs

import com.jfoenix.controls.JFXTextField
import javafx.scene.layout.Region

class FinancerTextInputDialog @JvmOverloads constructor(headerText: String? = null, private val placeholder: String? = null) : FinancerDialog<String?>(null) {
    private var textField: JFXTextField? = null
    protected override val dialogContent: Region?
        protected get() {
            textField = JFXTextField()
            textField!!.id = "inputDialogTextField"
            return textField
        }

    override fun prepareDialogContent() {
        textField!!.text = placeholder
    }

    override fun checkConsistency(): Boolean {
        return true
    }

    override fun onConfirm() {
        value = textField!!.text
        super.onConfirm()
    }

    init {
        setDialogTitle(headerText)
        prepareDialogContent()
    }
}
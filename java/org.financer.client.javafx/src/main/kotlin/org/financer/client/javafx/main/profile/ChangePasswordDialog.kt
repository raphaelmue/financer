package org.financer.client.javafx.main.profile

import com.jfoenix.controls.JFXPasswordField
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Region
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N.get
import org.financer.client.javafx.dialogs.FinancerDialog
import org.financer.shared.domain.model.value.objects.HashedPassword

internal class ChangePasswordDialog(user: User?) : FinancerDialog<User?>(user) {
    private var oldPasswordField: JFXPasswordField? = null
    private var newPasswordField: JFXPasswordField? = null
    private var repeatNewPasswordField: JFXPasswordField? = null
    override fun checkConsistency(): Boolean {
        val oldPassword = oldPasswordField!!.text
        if (!value!!.password!!.isEqualTo(oldPassword)) {
            this.errorMessage = get("errPasswordWrong")
            return false
        }
        if (newPasswordField!!.text != repeatNewPasswordField!!.text) {
            this.errorMessage = get("errPasswordsDoNotMatch")
            return false
        }
        if (oldPasswordField!!.text == newPasswordField!!.text) {
            this.errorMessage = get("errPasswordsMustDistinguish")
            return false
        }
        return true
    }

    override val dialogContent: Region
        get() {
            val gridPane = GridPane()
            gridPane.vgap = 8.0
            gridPane.hgap = 50.0
            gridPane.add(Label(get("oldPassword")), 0, 0)
            oldPasswordField = JFXPasswordField()
            oldPasswordField!!.id = "oldPasswordField"
            gridPane.add(oldPasswordField, 1, 0)
            gridPane.add(Label(get("newPassword")), 0, 1)
            newPasswordField = JFXPasswordField()
            newPasswordField!!.id = "newPasswordField"
            gridPane.add(newPasswordField, 1, 1)
            gridPane.add(Label(get("repeatNewPasswort")), 0, 2)
            repeatNewPasswordField = JFXPasswordField()
            repeatNewPasswordField!!.id = "repeatNewPasswordField"
            gridPane.add(repeatNewPasswordField, 1, 2)
            return gridPane
        }

    override fun onConfirm() {
        value!!.setPassword(HashedPassword(newPasswordField!!.text))
        super.onConfirm()
    }

    init {
        setDialogTitle(get("password"))
    }
}
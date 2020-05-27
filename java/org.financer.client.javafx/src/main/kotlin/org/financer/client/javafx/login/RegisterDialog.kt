package org.financer.client.javafx.login

import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXPasswordField
import com.jfoenix.controls.JFXTextField
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Region
import javafx.util.StringConverter
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.DatePicker
import org.financer.client.javafx.dialogs.FinancerDialog
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.shared.domain.model.value.objects.*

class RegisterDialog internal constructor() : FinancerDialog<User?>(null, (LoginController.instance as LoginController).rootLayout) {
    private var nameField: JFXTextField? = null
    private var surnameField: JFXTextField? = null
    private var emailField: JFXTextField? = null
    private var passwordField: JFXPasswordField? = null
    private var passwordRepeatField: JFXPasswordField? = null
    private var birthDatePicker: DatePicker? = null
    private var genderComboBox: JFXComboBox<Gender>? = null
    override val dialogContent: Region
        get() {
            val gridPane = GridPane()
            gridPane.vgap = 8.0
            gridPane.hgap = 150.0
            gridPane.add(Label(get("name")), 0, 0)
            nameField = JFXTextField()
            nameField!!.id = "registerNameTextField"
            gridPane.add(nameField, 1, 0)
            gridPane.add(Label(get("surname")), 0, 1)
            surnameField = JFXTextField()
            surnameField!!.id = "registerSurnameTextField"
            gridPane.add(surnameField, 1, 1)
            gridPane.add(Label(get("email")), 0, 2)
            emailField = JFXTextField()
            emailField!!.id = "registerEmailTextField"
            gridPane.add(emailField, 1, 2)
            gridPane.add(Label(get("birthdate")), 0, 3)
            birthDatePicker = DatePicker(JavaFXFormatter(LocalStorageImpl.getInstance()))
            birthDatePicker!!.id = "registerBirthDatePicker"
            gridPane.add(birthDatePicker, 1, 3)
            gridPane.add(Label(get("gender")), 0, 4)
            genderComboBox = JFXComboBox()
            genderComboBox!!.id = "genderComboBox"
            genderComboBox!!.items.addAll(Gender.getAll())
            genderComboBox!!.setConverter(object : StringConverter<Gender>() {
                override fun toString(gender: Gender): String {
                    return get(gender.gender.getName())
                }

                override fun fromString(name: String): Gender? {
                    return Gender(name)
                }
            })
            genderComboBox!!.placeholder = Label(get("gender"))
            gridPane.add(genderComboBox, 1, 4)
            gridPane.add(Label(get("password")), 0, 5)
            passwordField = JFXPasswordField()
            passwordField!!.id = "registerPasswordTextField"
            gridPane.add(passwordField, 1, 5)
            gridPane.add(Label(get("repeatPassword")), 0, 6)
            passwordRepeatField = JFXPasswordField()
            passwordRepeatField!!.id = "registerRepeatPasswordTextField"
            gridPane.add(passwordRepeatField, 1, 6)
            return gridPane
        }

    override fun checkConsistency(): Boolean {
        var result = true
        if (nameField!!.text.isEmpty() || surnameField!!.text.isEmpty() ||
                emailField!!.text.isEmpty() || passwordField!!.text.isEmpty() ||
                passwordRepeatField!!.text.isEmpty()) {
            this.errorMessage = get("errFillRequiredFields")
            result = false
        }
        if (passwordField!!.text.length < 8) {
            this.errorMessage = get("errInvalidPasswordLength")
            result = false
        }
        if (passwordField!!.text != passwordRepeatField!!.text) {
            this.errorMessage = get("errPasswordsDoNotMatch")
            result = false
        }
        return result
    }

    override fun onConfirm() {
        value = User()
                .setId(0)
                .setEmail(Email(emailField!!.text))
                .setPassword(HashedPassword(passwordField!!.text))
                .setName(Name(nameField!!.text, surnameField!!.text))
                .setBirthDate(BirthDate(birthDatePicker!!.value))
                .setGender(genderComboBox!!.selectionModel.selectedItem)
        super.onConfirm()
    }

    init {
        setDialogTitle(get("registration"))
    }
}
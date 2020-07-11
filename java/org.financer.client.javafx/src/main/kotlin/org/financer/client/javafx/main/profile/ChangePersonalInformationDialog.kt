package org.financer.client.javafx.main.profile

import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXDatePicker
import com.jfoenix.controls.JFXTextField
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Region
import javafx.util.StringConverter
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N.get
import org.financer.client.javafx.dialogs.FinancerDialog
import org.financer.shared.domain.model.value.objects.BirthDate
import org.financer.shared.domain.model.value.objects.Gender
import org.financer.shared.domain.model.value.objects.Name

class ChangePersonalInformationDialog(value: User?) : FinancerDialog<User?>(value) {
    private var nameField: JFXTextField? = null
    private var surnameField: JFXTextField? = null
    private var birthDatePicker: JFXDatePicker? = null
    private var genderComboBox: JFXComboBox<Gender>? = null
    override fun prepareDialogContent() {
        nameField!!.text = value!!.name!!.firstName
        surnameField!!.text = value!!.name!!.surname
        birthDatePicker!!.value = value!!.birthDate!!.birthDate
        genderComboBox!!.value = value!!.gender
    }

    override val dialogContent: Region
        get() {
            val gridPane = GridPane()
            gridPane.hgap = 80.0
            gridPane.vgap = 8.0
            gridPane.add(Label(get("name")), 0, 0)
            nameField = JFXTextField()
            nameField!!.id = "nameField"
            gridPane.add(nameField, 1, 0)
            gridPane.add(Label(get("surname")), 0, 1)
            surnameField = JFXTextField()
            surnameField!!.id = "surnameField"
            gridPane.add(surnameField, 1, 1)
            gridPane.add(Label(get("birthdate")), 0, 2)
            birthDatePicker = JFXDatePicker()
            birthDatePicker!!.id = "registerBirthDatePicker"
            gridPane.add(birthDatePicker, 1, 2)
            gridPane.add(Label(get("gender")), 0, 3)
            genderComboBox = JFXComboBox()
            genderComboBox!!.id = "genderComboBox"
            genderComboBox!!.items.addAll(Gender.getAll())
            genderComboBox!!.setConverter(object : StringConverter<Gender>() {
                override fun toString(gender: Gender): String {
                    return get(gender.gender.getName())
                }

                override fun fromString(name: String): Gender {
                    return Gender(name)
                }
            })
            genderComboBox!!.placeholder = Label(get("gender"))
            gridPane.add(genderComboBox, 1, 3)
            return gridPane
        }

    override fun checkConsistency(): Boolean {
        var result = true
        if (nameField!!.text.isEmpty() || surnameField!!.text.isEmpty() || birthDatePicker!!.value == null) {
            this.errorMessage = get("errFillRequiredFields")
            result = false
        }
        return result
    }

    override fun onConfirm() {
        value!!.setName(Name(nameField!!.text, surnameField!!.text))
        value!!.setBirthDate(BirthDate(birthDatePicker!!.value))
        value!!.setGender(genderComboBox!!.value)
        super.onConfirm()
    }

    init {
        setDialogTitle(get("personalInformation"))
        prepareDialogContent()
    }
}
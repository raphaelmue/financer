package org.financer.client.desktop.view.login

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import org.financer.client.desktop.controller.login.LoginController
import tornadofx.*

class LoginRequest(val email: String, val password: String) : FXEvent(EventBus.RunOn.BackgroundThread)

class LoginView : View("Login") {

    private val model = ViewModel()
    private val email = model.bind { SimpleStringProperty() }
    private val password = model.bind { SimpleStringProperty() }
    private val loginController: LoginController by inject()

    override val root = form {
        fieldset(labelPosition = Orientation.VERTICAL) {
            field(messages["email"]) {
                textfield(email).required()
            }
            field(messages["password"]) {
                passwordfield(password).required()
            }
            button("Log in") {
                enableWhen(model.valid)
                isDefaultButton = true
                useMaxWidth = true
                action {
                    fire(LoginRequest(email.value, password.value))
                }
            }
        }
        label(loginController.statusProperty) {
            style {
                paddingTop = 10
                textFill = Color.RED
                fontWeight = FontWeight.BOLD
            }
        }
    }

    override fun onDock() {
        email.value = ""
        password.value = ""
        model.clearDecorators()
    }
}
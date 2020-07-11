package org.financer.client.desktop.view.login

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import org.financer.client.desktop.controller.login.LoginController
import org.financer.client.domain.model.user.User
import org.financer.shared.domain.model.value.objects.Name
import tornadofx.*


class MainView : View() {
    private val user = User().setName(Name("Test", ""))
    private val loginController: LoginController by inject()

    override val root = vbox(10) {
        setPrefSize(800.0, 600.0)
        alignment = Pos.CENTER

        label(user.name!!.firstName) {
            style {
                fontWeight = FontWeight.BOLD
                fontSize = 24.px
            }
        }

        button("Logout").action(loginController::logout)
    }
}
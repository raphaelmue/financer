package org.financer.client.desktop.controller.login

import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.runBlocking
import org.financer.client.desktop.view.login.LoginRequest
import org.financer.client.desktop.view.login.LoginView
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.user.User
import tornadofx.*

class LoginController : Controller() {

    val statusProperty = SimpleStringProperty()
    private var status by statusProperty

    init {
        subscribe<LoginRequest> { event ->
            handleLoginRequest(event.email, event.password)
        }
        subscribe<LoginEvent> { event ->
            handleLoginEvent(event.user)
        }
        subscribe<InvalidLoginEvent> {
            status = messages["incorrectCredentials"]
        }
    }

    private val api: RestApi = RestApiImpl()

    private fun handleLoginRequest(email: String, password: String) {
        val response = runBlocking { api.loginUser(email, password) }
        if (response != null) {
            fire(LoginEvent(response))
        } else {
            fire(InvalidLoginEvent)
        }
    }

    private fun handleLoginEvent(user: User) {
        status = "Success!"
    }

    fun logout() {
        primaryStage.uiComponent<UIComponent>()?.replaceWith(LoginView::class, sizeToScene = true, centerOnScreen = true)
    }
}

class LoginEvent(val user: User) : FXEvent()

object InvalidLoginEvent : FXEvent()
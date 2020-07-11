package org.financer.client.desktop

import org.financer.client.connection.ServerRequest
import org.financer.client.desktop.error.ErrorHandler
import org.financer.client.desktop.local.LocalStorageImpl
import org.financer.client.desktop.view.login.LoginView
import tornadofx.*

class FinancerApplication : App(LoginView::class) {
    init {
        ServerRequest.setLocalStorage(LocalStorageImpl())
        Thread.setDefaultUncaughtExceptionHandler(ErrorHandler())
    }
}
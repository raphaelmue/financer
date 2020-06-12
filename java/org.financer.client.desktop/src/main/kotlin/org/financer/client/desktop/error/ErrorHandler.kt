package org.financer.client.desktop.error

import javafx.scene.control.Alert
import tornadofx.*
import java.util.logging.Level
import java.util.logging.Logger

class ErrorHandler : Thread.UncaughtExceptionHandler {
    private val logger = Logger.getLogger("org.financer.client.desktop")


    override fun uncaughtException(t: Thread?, e: Throwable?) {
        if (e != null) {
            logger.log(Level.SEVERE, e.message, e)
            alert(Alert.AlertType.ERROR, "Error", e.message)
        }
    }
}
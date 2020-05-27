package org.financer.client.javafx.util

import javafx.application.Platform
import javafx.application.Preloader.ProgressNotification
import javafx.stage.Stage
import org.financer.client.javafx.main.FinancerApplication
import java.util.logging.Level
import java.util.logging.Logger

object ApplicationHelper {
    private val logger = Logger.getLogger("FinancerApplication")
    @JvmStatic
    fun restartApplication(stage: Stage) {
        stage.close()
        Platform.runLater {
            val financerApplication = FinancerApplication()
            try {
                financerApplication.init()
                financerApplication.start(Stage())
                financerApplication.notifyPreloader(ProgressNotification(0.0))
            } catch (e: Exception) {
                logger.log(Level.SEVERE, e.message, e)
            }
        }
    }
}
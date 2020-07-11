package org.financer.client.javafx.main

import com.jfoenix.controls.JFXProgressBar
import javafx.application.Preloader
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.financer.client.connection.ServerRequest
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N
import org.financer.client.format.I18N.get
import org.financer.client.javafx.local.LocalStorageImpl
import java.io.Serializable

class SplashScreenLoader : Preloader() {
    private var progressBar: JFXProgressBar? = null
    private var infoMessage: Label? = null
    private var noLoadingProgress = true
    private var primaryStage: Stage? = null
    private val content: Parent
        get() {
            val borderPane = BorderPane()
            borderPane.style = "-fx-background-color: -fx-color-white; -fx-padding: 30px; -fx-background-radius: 20px"
            val heading = Label("F I N A N C E R")
            heading.alignment = Pos.CENTER
            heading.textAlignment = TextAlignment.CENTER
            heading.style = "-fx-text-fill: -fx-color-primary; -fx-font-size: 22px; -fx-padding: 15px 50px; -fx-font-weight: 700; -fx-font-family: 'Roboto Medium'"
            val image = Image(javaClass.getResourceAsStream("/images/icons/financer-icon-clipart.png"))
            val imageView = ImageView(image)
            imageView.fitHeight = 120.0
            imageView.isPickOnBounds = true
            imageView.isPreserveRatio = true
            infoMessage = Label()
            infoMessage!!.style = "-fx-text-fill: -fx-color-dark-gray; -fx-font-size: 14px; -fx-padding: 15px 20px"
            progressBar = JFXProgressBar()
            progressBar!!.progress = -1.0
            progressBar!!.prefWidth = 500.0
            val hBox = HBox(imageView, heading)
            hBox.prefWidth = 500.0
            hBox.alignment = Pos.CENTER
            borderPane.center = hBox
            borderPane.bottom = VBox(infoMessage, progressBar)
            return borderPane
        }

    override fun start(primaryStage: Stage) {
        I18N.setLocalStorage(LocalStorageImpl.getInstance())
        ServerRequest.setLocalStorage(LocalStorageImpl.getInstance())
        val scene = Scene(content, 500.0, 300.0)
        scene.stylesheets.add(javaClass.getResource("style/colors.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("style/main.style.css").toExternalForm())
        scene.fill = Color.TRANSPARENT
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.scene = scene
        primaryStage.icons.add(Image(FinancerApplication::class.java.getResourceAsStream("/images/icons/financer-icon.png")))
        this.primaryStage = primaryStage
        if (LocalStorageImpl.getInstance().readObject<User>("user") == null) {
            this.primaryStage!!.hide()
        } else {
            this.primaryStage!!.show()
        }
    }

    override fun handleProgressNotification(pn: ProgressNotification) {
        if (pn.progress != 1.0 || !noLoadingProgress) {
            progressBar!!.progress = pn.progress / 2
            if (pn.progress > 0) {
                noLoadingProgress = false
            }
        } else {
            primaryStage!!.hide()
        }
    }

    override fun handleStateChangeNotification(evt: StateChangeNotification) {
        //ignore, hide after application signals it is ready
    }

    override fun handleApplicationNotification(preloaderNotification: PreloaderNotification) {
        if (preloaderNotification is ProgressNotification) {
            if (!primaryStage!!.isShowing) {
                primaryStage!!.show()
            }
            val progress = preloaderNotification.progress
            progressBar!!.progress = progress
            if (progress < 0.1) {
                infoMessage!!.text = get("loadingCategories")
            } else if (progress < 0.4) {
                infoMessage!!.text = get("loadingTransactions")
            } else {
                infoMessage!!.text = get("loadingFixedTransactions")
            }
        } else if (preloaderNotification is StateChangeNotification) {
            primaryStage!!.hide()
        }
    }
}
package org.financer.client.javafx.main

import javafx.application.Application
import javafx.application.Platform
import javafx.application.Preloader.ProgressNotification
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.category.CategoryRoot
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.local.LocalStorage
import org.financer.shared.domain.model.value.objects.SettingPair
import java.io.IOException
import java.io.Serializable
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class FinancerApplication : Application() {
    private val restApi: RestApi = RestApiImpl()
    private val localStorage: LocalStorage = LocalStorageImpl()
    private val logger = Logger.getLogger("FinancerApplication")
    private val ready: BooleanProperty = SimpleBooleanProperty(false)
    override fun init() {
        val user = LocalStorageImpl.getInstance().readObject<User>("user")
        if (user != null) {
            notifyPreloader(ProgressNotification(0.0))
            CoroutineScope(Dispatchers.IO).launch {
                val categories = restApi.getUsersCategories(user.getId())
                val variableTransactions = restApi.getUsersVariableTransactions(user.getId(), 0)
                val fixedTransactions = restApi.getUsersFixedTransactions(user.getId())
                localStorage.writeObject("categories", CategoryRoot(categories.toSet()))
            }
        }
    }

    @Throws(IOException::class)
    override fun start(primaryStage: Stage) {
        logger.log(Level.INFO, "Financer Application has started.")
        val user = LocalStorageImpl.getInstance().readObject<User>("user")
        if (user == null) {
            initLoginStage()
        } else {
            I18N.setLocalStorage(LocalStorageImpl.getInstance())
            ready.addListener { observableValue: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean ->
                if (java.lang.Boolean.TRUE == newValue) {
                    Platform.runLater {
                        try {
                            initMainStage()
                        } catch (e: IOException) {
                            logger.log(Level.SEVERE, e.message, e)
                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun initLoginStage() {
        // setting up language
        val resourceBundle = ResourceBundle.getBundle("Financer", Locale.ENGLISH)

        // loading FXML file
        val root = FXMLLoader.load<Parent>(javaClass.getResource("/org/financer/client/javafx/main/views/login.fxml"), resourceBundle)
        val primaryStage = Stage()
        val scene = Scene(root, 800.0, 500.0)
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/colors.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/form.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/login.style.css").toExternalForm())
        scene.fill = Color.TRANSPARENT
        primaryStage.icons.add(Image(FinancerApplication::class.java.getResourceAsStream("/images/icons/financer-icon.png")))
        Font.loadFont(javaClass.getResource("/fonts/Roboto-Regular.ttf").toExternalForm(), 12.0)
        Font.loadFont(javaClass.getResource("/fonts/Roboto-Medium.ttf").toExternalForm(), 12.0)
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.title = "Financer - Login"
        primaryStage.scene = scene
        primaryStage.show()
    }

    @Throws(IOException::class)
    private fun initMainStage() {
        val resourceBundle = ResourceBundle.getBundle("Financer",
                (LocalStorageImpl.getInstance().readObject<Serializable>("user") as User?)!!.getValueOrDefault<Any>(SettingPair.Property.LANGUAGE) as Locale)
        val root = FXMLLoader.load<Parent>(javaClass.getResource("views/financer.fxml"), resourceBundle)
        val primaryStage = Stage()
        val scene = Scene(root, 1300.0, 700.0)
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/colors.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/form.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/navbar.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/footer.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/header.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/main.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/transactions.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/statistics.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/overview.style.css").toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/org/financer/client/javafx/main/style/profile.style.css").toExternalForm())
        primaryStage.icons.add(Image(FinancerApplication::class.java.getResourceAsStream("/images/icons/financer-icon.png")))
        Font.loadFont(javaClass.getResource("/fonts/Roboto-Regular.ttf").toExternalForm(), 12.0)
        Font.loadFont(javaClass.getResource("/fonts/Roboto-Medium.ttf").toExternalForm(), 12.0)
        primaryStage.title = "Financer"
        primaryStage.scene = scene
        primaryStage.show()
    }
}
package org.financer.client.javafx.main

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXHamburger
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition
import javafx.animation.ParallelTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.util.Duration
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.connection.ServerRequest
import org.financer.client.connection.ServerRequest.Companion.application
import org.financer.client.domain.model.user.User
import org.financer.client.format.FormatterImpl
import org.financer.client.format.I18N.get
import org.financer.client.javafx.dialogs.FinancerExceptionDialog
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.local.Application
import org.financer.shared.domain.model.value.objects.SettingPair
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.Logger

class FinancerController : Initializable, Application {
    @FXML
    var rootLayout: BorderPane? = null

    @FXML
    var header: BorderPane? = null

    @FXML
    var overviewTabBtn: Button? = null

    @FXML
    var overviewTabIconBtn: JFXButton? = null

    @FXML
    var transactionsTabBtn: JFXButton? = null

    @FXML
    var transactionsTabIconBtn: JFXButton? = null

    @FXML
    var statisticsTabBtn: JFXButton? = null

    @FXML
    var statisticsTabIconBtn: JFXButton? = null

    @FXML
    var profileTabBtn: JFXButton? = null

    @FXML
    var profileTabIconBtn: JFXButton? = null

    @FXML
    var settingTabBtn: JFXButton? = null

    @FXML
    var settingTabIconBtn: JFXButton? = null

    @FXML
    var accountMenuBtn: MenuButton? = null

    @FXML
    var logoutBtn: MenuItem? = null

    @FXML
    var hamburgerBtn: JFXHamburger? = null

    @FXML
    var contentLabel: Label? = null

    @FXML
    var offlineLabel: Label? = null

    @FXML
    var navigationBox: VBox? = null

    @FXML
    var contentPane: BorderPane? = null
    private var resourceBundle: ResourceBundle? = null
    private val localStorage = LocalStorageImpl.getInstance() as LocalStorageImpl
    private var snackbar: JFXSnackbar? = null
    private var isNavigationBarHidden = false

    @FXML
    override fun initialize(location: URL, resources: ResourceBundle) {
        instance = this
        application = this
        ServerRequest.setLocalStorage(localStorage)
        val user = localStorage.readObject<User>("user")

        // setting up language
        resourceBundle = ResourceBundle.getBundle("Financer", user!!.getValueOrDefault<Any>(SettingPair.Property.LANGUAGE) as Locale)
        snackbar = JFXSnackbar(rootLayout)
        try {
            loadingBox = FXMLLoader.load(javaClass.getResource("/org/financer/client/javafx/main/views/loading.fxml"), resourceBundle)
        } catch (e: IOException) {
            logger.log(Level.SEVERE, e.message, e)
        }
        accountMenuBtn!!.text = FormatterImpl(localStorage).format(user.name!!)
        val fontAwesome = GlyphFontRegistry.font("FontAwesome")
        accountMenuBtn!!.graphic = fontAwesome.create(FontAwesome.Glyph.USER)
        accountMenuBtn!!.graphicTextGap = 10.0
        logoutBtn!!.graphic = fontAwesome.create(FontAwesome.Glyph.SIGN_OUT)
        val burgerTask = HamburgerSlideCloseTransition(hamburgerBtn)
        burgerTask.rate = 1.0
        Platform.runLater { burgerTask.play() }
        hamburgerBtn!!.animation = burgerTask
        hamburgerBtn!!.addEventHandler(MouseEvent.MOUSE_PRESSED) { e: MouseEvent? ->
            burgerTask.rate = burgerTask.rate * -1
            burgerTask.play()
        }
        handleShowOverviewContent()
    }

    override fun showLoadingBox() {
        if (loadingBox != null) {
            loadingBox!!.isVisible = true
        }
    }

    override fun hideLoadingBox() {
        if (loadingBox != null && loadingBox!!.isVisible) {
            loadingBox!!.isVisible = false
        }
    }

    override fun setOffline() {
//        Platform.runLater(() -> {
//            this.header.setStyle("-fx-background-color: #909ca8");
//            this.offlineLabel.setVisible(true);
//        });
    }

    override fun setOnline() {
//        Platform.runLater(() -> {
//            this.header.setStyle("-fx-background-color: #44a1a0");
//            this.offlineLabel.setVisible(false);
//        });
    }

    @Synchronized
    override fun showToast(messageType: Application.MessageType?, message: String?) {
        val messageLabel = Label(message)
        messageLabel.styleClass.add(messageType!!.type + "-toast-label")
        snackbar!!.enqueue(SnackbarEvent(messageLabel))
    }

    override fun showErrorDialog(exception: Exception?) {
        Platform.runLater { FinancerExceptionDialog("Financer", exception) }
    }

    fun handleShowOverviewContent() {
        loadFXML(javaClass.getResource("/org/financer/client/javafx/main/views/overview.fxml"))
        removeSelectedStyleClass()
        overviewTabBtn!!.styleClass.add("selected")
        overviewTabIconBtn!!.styleClass.add("selected")
        contentLabel!!.text = get("overview")
    }

    fun handleShowTransactionsContent() {
        loadFXML(javaClass.getResource("/org/financer/client/javafx/main/views/transactions.fxml"))
        removeSelectedStyleClass()
        transactionsTabBtn!!.styleClass.add("selected")
        transactionsTabIconBtn!!.styleClass.add("selected")
        contentLabel!!.text = get("transactions")
    }

    fun handleShowStatisticsContent() {
        loadFXML(javaClass.getResource("/org/financer/client/javafx/main/views/statistics.fxml"))
        removeSelectedStyleClass()
        statisticsTabBtn!!.styleClass.add("selected")
        statisticsTabIconBtn!!.styleClass.add("selected")
        contentLabel!!.text = get("statistics")
    }

    fun handleShowProfileContent() {
        loadFXML(javaClass.getResource("/org/financer/client/javafx/main/views/profile.fxml"))
        removeSelectedStyleClass()
        profileTabBtn!!.styleClass.add("selected")
        profileTabIconBtn!!.styleClass.add("selected")
        contentLabel!!.text = get("profile")
    }

    fun handleShowSettingsContent() {
        loadFXML(javaClass.getResource("/org/financer/client/javafx/main/views/settings.fxml"))
        removeSelectedStyleClass()
        settingTabBtn!!.styleClass.add("selected")
        settingTabIconBtn!!.styleClass.add("selected")
        contentLabel!!.text = get("settings")
    }

    private fun removeSelectedStyleClass() {
        val navBtnClass = "nav-btn"
        overviewTabBtn!!.styleClass.clear()
        overviewTabBtn!!.styleClass.add(navBtnClass)
        transactionsTabBtn!!.styleClass.clear()
        transactionsTabBtn!!.styleClass.add(navBtnClass)
        statisticsTabBtn!!.styleClass.clear()
        statisticsTabBtn!!.styleClass.add(navBtnClass)
        profileTabBtn!!.styleClass.clear()
        profileTabBtn!!.styleClass.add(navBtnClass)
        settingTabBtn!!.styleClass.clear()
        settingTabBtn!!.styleClass.add(navBtnClass)
        overviewTabIconBtn!!.styleClass.clear()
        overviewTabIconBtn!!.styleClass.add(navBtnClass)
        transactionsTabIconBtn!!.styleClass.clear()
        transactionsTabIconBtn!!.styleClass.add(navBtnClass)
        statisticsTabIconBtn!!.styleClass.clear()
        statisticsTabIconBtn!!.styleClass.add(navBtnClass)
        profileTabIconBtn!!.styleClass.clear()
        profileTabIconBtn!!.styleClass.add(navBtnClass)
        settingTabIconBtn!!.styleClass.clear()
        settingTabIconBtn!!.styleClass.add(navBtnClass)
    }

    private fun loadFXML(url: URL) {
        try {
            val stackPane = StackPane()
            stackPane.children.add(FXMLLoader.load(url, resourceBundle))
            stackPane.children.add(loadingBox)
            contentPane!!.center = stackPane
            BorderPane.setMargin(stackPane, Insets(20.0, 0.0, 0.0, 20.0))
        } catch (e: IOException) {
            logger.log(Level.SEVERE, e.message, e)
        }
    }

    fun handleLogoutBtn() {
        handleLogout()
    }

    fun onToggleNavigationBar() {
        overviewTabIconBtn!!.isVisible = !isNavigationBarHidden
        transactionsTabIconBtn!!.isVisible = !isNavigationBarHidden
        statisticsTabIconBtn!!.isVisible = !isNavigationBarHidden
        profileTabIconBtn!!.isVisible = !isNavigationBarHidden
        settingTabIconBtn!!.isVisible = !isNavigationBarHidden
        val duration = Duration(350.0)
        val t1 = TranslateTransition(duration, navigationBox)
        val t2 = TranslateTransition(duration, contentPane)
        val tabIconBtnTransitions: MutableList<TranslateTransition> = ArrayList()
        tabIconBtnTransitions.add(TranslateTransition(duration, overviewTabIconBtn))
        tabIconBtnTransitions.add(TranslateTransition(duration, transactionsTabIconBtn))
        tabIconBtnTransitions.add(TranslateTransition(duration, statisticsTabIconBtn))
        tabIconBtnTransitions.add(TranslateTransition(duration, profileTabIconBtn))
        tabIconBtnTransitions.add(TranslateTransition(duration, settingTabIconBtn))
        val tabBtnTransitions: MutableList<TranslateTransition> = ArrayList()
        tabBtnTransitions.add(TranslateTransition(duration, overviewTabBtn))
        tabBtnTransitions.add(TranslateTransition(duration, transactionsTabBtn))
        tabBtnTransitions.add(TranslateTransition(duration, statisticsTabBtn))
        tabBtnTransitions.add(TranslateTransition(duration, profileTabBtn))
        tabBtnTransitions.add(TranslateTransition(duration, settingTabBtn))
        if (!isNavigationBarHidden) {
            t1.toX = -180.0
            t2.toX = -90.0
            tabBtnTransitions.forEach(Consumer { translateTransition: TranslateTransition -> translateTransition.toX = -180.0 })
            tabIconBtnTransitions.forEach(Consumer { translateTransition: TranslateTransition ->
                translateTransition.fromX = -180.0
                translateTransition.toX = 180.0
            })
            overviewTabIconBtn!!.toFront()
            transactionsTabIconBtn!!.toFront()
            statisticsTabIconBtn!!.toFront()
            profileTabIconBtn!!.toFront()
            settingTabIconBtn!!.toFront()
        } else {
            t1.toX = 0.0
            t2.toX = 0.0
            tabBtnTransitions.forEach(Consumer { translateTransition: TranslateTransition -> translateTransition.toX = 0.0 })
            tabIconBtnTransitions.forEach(Consumer { translateTransition: TranslateTransition -> translateTransition.toX = 0.0 })
            overviewTabIconBtn!!.toBack()
            transactionsTabIconBtn!!.toBack()
            statisticsTabIconBtn!!.toBack()
            profileTabIconBtn!!.toBack()
            settingTabIconBtn!!.toBack()
        }
        val transition = ParallelTransition(t1, t2)
        transition.children.addAll(tabBtnTransitions)
        transition.children.addAll(tabIconBtnTransitions)
        transition.play()
        isNavigationBarHidden = !isNavigationBarHidden
    }

    companion object {
        private var loadingBox: VBox? = null
        var instance: Application? = null
            private set
        private val logger = Logger.getLogger("FinancerApplication")

        fun handleLogout() {
            val stage = loadingBox!!.scene.window as Stage
            stage.close()
            LocalStorageImpl.getInstance().deleteAllData()
            try {
                FinancerApplication().start(Stage())
            } catch (e: IOException) {
                logger.log(Level.SEVERE, e.message, e)
            }
        }
    }
}
package org.financer.client.javafx.login

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXPasswordField
import com.jfoenix.controls.JFXTextField
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.connection.ServerRequest
import org.financer.client.connection.ServerRequest.Companion.application
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N
import org.financer.client.format.I18N.get
import org.financer.client.javafx.dialogs.FinancerExceptionDialog
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.javafx.util.ApplicationHelper
import org.financer.client.local.Application
import org.financer.util.collections.Action
import java.net.URL
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class LoginController : Initializable, Application {
    @FXML
    var rootLayout: StackPane? = null

    @FXML
    var loginEmailTextField: JFXTextField? = null

    @FXML
    var loginPasswordField: JFXPasswordField? = null

    @FXML
    var loginErrorLabel: Label? = null

    @FXML
    var progressIndicatorBox: VBox? = null

    @FXML
    var loginBtn: JFXButton? = null

    @FXML
    var openRegisterDialogBtn: JFXButton? = null

    @FXML
    var closeBtn: JFXButton? = null
    private val restApi: RestApi = RestApiImpl()
    private val logger = Logger.getLogger("FinancerApplication")
    private val localStorage = LocalStorageImpl.getInstance() as LocalStorageImpl
    private var xOffset = 0.0
    private var yOffset = 0.0
    override fun initialize(location: URL, resources: ResourceBundle) {
        I18N.setLocalStorage(localStorage)
        instance = this
        application = this
        ServerRequest.setLocalStorage(localStorage)
        Platform.runLater {
            rootLayout!!.scene.onKeyPressed = EventHandler { e: KeyEvent ->
                if (e.code == KeyCode.ENTER) {
                    handleSignInButtonAction()
                }
            }
        }
        Platform.runLater {
            rootLayout!!.scene.onKeyPressed = EventHandler { e: KeyEvent ->
                if (e.code == KeyCode.ENTER) {
                    handleSignInButtonAction()
                }
            }
        }
        rootLayout!!.onMousePressed = EventHandler { event: MouseEvent ->
            xOffset = event.sceneX
            yOffset = event.sceneY
        }
        rootLayout!!.onMouseDragged = EventHandler { event: MouseEvent ->
            rootLayout!!.scene.window.x = event.screenX - xOffset
            rootLayout!!.scene.window.y = event.screenY - yOffset
        }
        loginBtn!!.text = loginBtn!!.text.toUpperCase()
        openRegisterDialogBtn!!.text = openRegisterDialogBtn!!.text.toUpperCase()
        val fontAwesome = GlyphFontRegistry.font("FontAwesome")
        closeBtn!!.graphic = fontAwesome.create(FontAwesome.Glyph.TIMES)
        closeBtn!!.onAction = EventHandler { event: ActionEvent? -> Platform.exit() }
    }

    fun handleSignInButtonAction() {
        logger.log(Level.INFO, "User's credentials will be checked ...")
        CoroutineScope(Dispatchers.IO).launch {
            val result = restApi.loginUser(loginEmailTextField!!.text, loginPasswordField!!.text)
            if (result != null) {
                withContext(Dispatchers.Main) { loginUser(result) }
            } else {
                logger.log(Level.INFO, "User's credentials are incorrect.")
                loginErrorLabel!!.isVisible = true
            }
        }
    }

    fun handleOpenRegisterDialog() {
        val dialog = RegisterDialog()
        dialog.setOnConfirm(Action { })
        dialog.setOnConfirm(Action { user: User? ->
            CoroutineScope(Dispatchers.IO).launch {
                val result = restApi.registerUser(user!!)
                withContext(Dispatchers.Main) { loginUser(result!!) }
            }
        })
    }

    private fun loginUser(user: User) {
        logger.log(Level.INFO, "User's credentials are correct.")
        loginErrorLabel!!.isVisible = false

        // storing user data
        localStorage.writeObject("user", user)
        ApplicationHelper.restartApplication(rootLayout!!.scene.window as Stage)
    }

    override fun showLoadingBox() {
        rootLayout!!.isDisable = true
        progressIndicatorBox!!.isVisible = true
    }

    override fun hideLoadingBox() {
        rootLayout!!.isDisable = false
        progressIndicatorBox!!.isVisible = false
    }

    override fun setOffline() {}
    override fun setOnline() {}
    override fun showToast(messageType: Application.MessageType?, message: String?) {
        throw UnsupportedOperationException("Showing a toast is not implemented yet!")
    }

    override fun showErrorDialog(exception: Exception?) {
        Platform.runLater { FinancerExceptionDialog(rootLayout, get("login"), exception) }
    }

    companion object {
        var instance: Application? = null
            private set
    }
}

package org.financer.client.javafx.main.settings

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXComboBox
import com.jfoenix.controls.JFXListView
import com.jfoenix.controls.JFXToggleButton
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.controlsfx.glyphfont.FontAwesome
import org.controlsfx.glyphfont.GlyphFontRegistry
import org.financer.client.domain.api.RestApi
import org.financer.client.domain.api.RestApiImpl
import org.financer.client.domain.model.user.Token
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N
import org.financer.client.format.I18N.Language.Companion.all
import org.financer.client.format.I18N.Language.Companion.getLanguageByLocale
import org.financer.client.format.I18N.get
import org.financer.client.javafx.dialogs.FinancerConfirmDialog
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.client.javafx.main.FinancerController
import org.financer.client.javafx.util.ApplicationHelper.restartApplication
import org.financer.shared.domain.model.value.objects.SettingPair
import org.financer.util.collections.Action
import java.net.URL
import java.util.*

class SettingsController : Initializable {
    @FXML
    var languageMenuComboBox: JFXComboBox<I18N.Language?>? = null

    @FXML
    var currencyComboBox: JFXComboBox<Currency>? = null

    @FXML
    var showSignCheckbox: JFXToggleButton? = null

    @FXML
    var maxNumberOfMonthsDisplayedComboBox: JFXComboBox<Long>? = null

    @FXML
    var logoutFromDeviceBtn: JFXButton? = null

    @FXML
    var devicesListView: JFXListView<Token>? = null

    @FXML
    var changeAmountSignAutomaticallyCheckBox: JFXToggleButton? = null
    private val restApi: RestApi = RestApiImpl()
    private val localStorage = LocalStorageImpl.getInstance()
    private var user: User = localStorage.readObject("user") ?: throw IllegalStateException("No User stored!");

    override fun initialize(location: URL, resources: ResourceBundle) {
        CoroutineScope(Dispatchers.Default).launch {
            FinancerController.instance!!.showLoadingBox()
            val fontAwesome = GlyphFontRegistry.font("FontAwesome")
            logoutFromDeviceBtn!!.graphic = fontAwesome.create(FontAwesome.Glyph.SIGN_OUT)
            languageMenuComboBox!!.items.addAll(all)
            languageMenuComboBox!!.selectionModel.select(getLanguageByLocale(user.getValueOrDefault(SettingPair.Property.LANGUAGE)))
            languageMenuComboBox!!.selectionModel.selectedItemProperty().addListener { _: ObservableValue<out I18N.Language?>?, _: I18N.Language?, newValue: I18N.Language? ->
                user.putOrUpdateSettingProperty(SettingPair.Property.LANGUAGE, newValue!!.locale)
                updateSettings()
                val dialog = FinancerConfirmDialog(get("warnChangesAfterRestart"))
                dialog.setOnConfirm(Action { result: Boolean? -> restartApplication((languageMenuComboBox!!.scene.window as Stage)) })
            }
            currencyComboBox!!.items.addAll(Currency.getAvailableCurrencies())
            currencyComboBox!!.items.sortWith(Comparator { o1: Currency, o2: Currency -> String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString()) })
            currencyComboBox!!.selectionModel.select(user.getValueOrDefault<Currency>(SettingPair.Property.CURRENCY))
            currencyComboBox!!.selectionModel.selectedItemProperty().addListener { _: ObservableValue<out Currency>?, _: Currency?, newValue: Currency ->
                user.putOrUpdateSettingProperty(SettingPair.Property.CURRENCY, newValue)
                showSignCheckbox!!.isDisable = false
                updateSettings()
            }
            if (!user.isPropertySet(SettingPair.Property.CURRENCY)) {
                showSignCheckbox!!.isDisable = true
            }
            showSignCheckbox!!.isSelected = user.getValueOrDefault(SettingPair.Property.SHOW_CURRENCY_SIGN)
            showSignCheckbox!!.selectedProperty().addListener { _: ObservableValue<out Boolean>?, _: Boolean?, newValue: Boolean ->
                user.putOrUpdateSettingProperty(SettingPair.Property.SHOW_CURRENCY_SIGN, newValue)
                updateSettings()
            }
            for (i in 3..8) maxNumberOfMonthsDisplayedComboBox!!.items.add(i.toLong())
            maxNumberOfMonthsDisplayedComboBox!!.selectionModel.select(user.getValueOrDefault(SettingPair.Property.MAX_NUMBER_OF_MONTHS_DISPLAYED) as Long)
            maxNumberOfMonthsDisplayedComboBox!!.selectionModel.selectedItemProperty().addListener { _: ObservableValue<out Long>?, _: Long?, newValue: Long ->
                user.putOrUpdateSettingProperty(SettingPair.Property.MAX_NUMBER_OF_MONTHS_DISPLAYED, newValue)
                updateSettings()
            }
            changeAmountSignAutomaticallyCheckBox!!.isSelected = user.getValueOrDefault(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY)
            changeAmountSignAutomaticallyCheckBox!!.selectedProperty().addListener { _: ObservableValue<out Boolean>?, _: Boolean?, newValue: Boolean ->
                user.putOrUpdateSettingProperty(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY, newValue)
                updateSettings()
            }
            loadTokenListView()
            FinancerController.instance!!.hideLoadingBox()
        }
    }

    private fun loadTokenListView() {
        devicesListView!!.selectionModel.selectedItemProperty().addListener { _: ObservableValue<out Token>?, _: Token?, _: Token? -> logoutFromDeviceBtn!!.isDisable = false }
        devicesListView!!.items = FXCollections.observableArrayList(user!!.tokens)
        devicesListView!!.setCellFactory { param: ListView<Token>? -> TokenListViewImpl() }
    }

    private fun updateSettings() = CoroutineScope(Dispatchers.IO).launch {
        if (user?.settings != null) {
            val result = restApi.updateUsersSettings(user!!.getId(), user!!.settings!!.toMutableMap())
            if (result != null) {
                localStorage.writeObject("user", result)
                user = result
            }
        }
    }

    fun handleLogoutFromDevice() {
        val dialog = FinancerConfirmDialog(get("confirmLogDeviceOut"))
        dialog.setOnConfirm(Action {
            CoroutineScope(Dispatchers.IO).launch {
                restApi.deleteToken(user!!.getId(), devicesListView!!.selectionModel.selectedItem.id)
                withContext(Dispatchers.Main) { devicesListView!!.items.remove(devicesListView!!.selectionModel.selectedItem) }
            }
        })
    }

    private open class TokenListViewImpl : ListCell<Token>() {
        override fun updateItem(item: Token, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty) {
                graphic = null
            } else {
                val borderPane = BorderPane()
                val left = VBox()
                val systemLabel = Label(item.operatingSystem.toString())
                val fontAwesome = GlyphFontRegistry.font("FontAwesome")
                systemLabel.graphic = fontAwesome.create(if (item.operatingSystem!!.operatingSystem.isMobile) FontAwesome.Glyph.MOBILE else FontAwesome.Glyph.DESKTOP)
                systemLabel.styleClass.add("list-cell-title")
                left.children.add(systemLabel)
                val ipAddressLabel = Label(item.ipAddress!!.ipAddress)
                left.children.add(ipAddressLabel)
                borderPane.left = left
                borderPane.center = Label(get("lastLogin") + " " + item.expireDate!!.expireDate.minusMonths(1).toString())
                graphic = borderPane
            }
        }
    }
}
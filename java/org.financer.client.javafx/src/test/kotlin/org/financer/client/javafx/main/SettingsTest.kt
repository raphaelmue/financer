package org.financer.client.javafx.main

import com.jfoenix.controls.JFXToggleButton
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.input.KeyCode
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.category.CategoryRoot
import org.financer.client.domain.model.transaction.VariableTransaction
import org.financer.client.domain.model.user.User
import org.financer.client.format.I18N
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.shared.domain.model.value.objects.SettingPair
import org.financer.util.collections.TreeUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.io.Serializable
import java.util.*
import java.util.function.BooleanSupplier

@Tag("integration")
class SettingsTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    public override fun setUpEach() {
        super.setUpEach()
    }

    @Test
    fun testChangeLanguage() {
        register(user(), password())
        clickOn(find<Node>("#settingTabBtn") as Button)
        var languageComboBox = find<ComboBox<I18N.Language>>("#languageMenuComboBox")
        clickOn(languageComboBox)
        clickOn(languageComboBox.items[1].language)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        val userToAssert = LocalStorageImpl.getInstance().readObject<User>("user")
        Assertions.assertEquals(languageComboBox.items[1].locale,
                userToAssert!!.getValueOrDefault(SettingPair.Property.LANGUAGE))
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertNotNull(clickOn("Überblick"))
        clickOn(find<Node>("#settingTabBtn") as Button)
        languageComboBox = find("#languageMenuComboBox")
        Assertions.assertEquals(userToAssert.getValueOrDefault(SettingPair.Property.LANGUAGE),
                languageComboBox.selectionModel.selectedItem.locale)
    }

    @Test
    fun testChangeCurrency() {
        register(user(), password())
        addCategory(variableCategory())
        addVariableTransaction(variableTransaction())
        clickOn(find<Node>("#settingTabBtn") as Button)
        val currencyComboBox = find<ComboBox<Currency>>("#currencyComboBox")
        clickOn(currencyComboBox)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        val userToAssert = LocalStorageImpl.getInstance().readObject<Serializable>("user") as User?
        Assertions.assertEquals(currencyComboBox.selectionModel.selectedItem, userToAssert!!.getValueOrDefault(SettingPair.Property.CURRENCY))
        clickOn(find<Node>("#transactionsTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        Assertions.assertNotNull(clickOn(String.format((userToAssert.getValueOrDefault<Any>(SettingPair.Property.LANGUAGE) as Locale),
                "%.2f", variableTransaction().amount.amount) +
                " " + (userToAssert.getValueOrDefault<Any>(SettingPair.Property.CURRENCY) as Currency).currencyCode))
    }

    @Test
    fun testChangeShowCurrencySign() {
        val transaction = variableTransaction()
        register(user(), password())
        addCategory(variableCategory())
        addVariableTransaction(transaction!!)
        clickOn(find<Node>("#settingTabBtn") as Button)
        clickOn(find<Node>("#showSignCheckbox") as JFXToggleButton)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        val userToAssert = LocalStorageImpl.getInstance().readObject<User>("user")
        Assertions.assertTrue(userToAssert!!.getValueOrDefault<BooleanSupplier>(SettingPair.Property.SHOW_CURRENCY_SIGN))
        clickOn(find<Node>("#transactionsTabBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        Assertions.assertNotNull(clickOn(String.format((userToAssert.getValueOrDefault<Any>(SettingPair.Property.LANGUAGE) as Locale),
                "%.2f", transaction.amount.amount) +
                " " + (userToAssert.getValueOrDefault<Any>(SettingPair.Property.CURRENCY) as Currency).symbol))
    }

    @Test
    fun testChangeChangeAmountSignAutomatically() {
        register(user(), password())
        addCategory(variableCategory())
        val transaction = variableTransaction()
        clickOn(find<Node>("#settingTabBtn") as Button)
        clickOn("Transaction Settings")
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        val changeAmountSignAutomaticallyCheckBox = find<JFXToggleButton>("#changeAmountSignAutomaticallyCheckBox")
        Assertions.assertFalse(changeAmountSignAutomaticallyCheckBox!!.isSelected)
        clickOn(changeAmountSignAutomaticallyCheckBox)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        val userToAssert = LocalStorageImpl.getInstance().readObject<User>("user")
        Assertions.assertTrue(userToAssert!!.getValueOrDefault<BooleanSupplier>(SettingPair.Property.CHANGE_AMOUNT_SIGN_AUTOMATICALLY))
        addVariableTransaction(transaction!!)
        Assertions.assertNotNull(clickOn(formatter!!.format(transaction.amount.multiply(-1.0))))
        val categoryRoot = LocalStorageImpl.getInstance().readObject<CategoryRoot>("categories")
        val transactions = (TreeUtil.getByValue(categoryRoot, variableCategory(),
                Comparator.comparingLong(Category::id)) as Category).transactions
        Assertions.assertEquals(1, transactions.size)
        for (transactionToAssert in transactions) {
            Assertions.assertTrue(transactionToAssert is VariableTransaction)
            Assertions.assertTrue(transactionToAssert.id > 0)
            Assertions.assertEquals(transaction.amount.multiply(-1.0), transactionToAssert.amount)
        }
    }
}
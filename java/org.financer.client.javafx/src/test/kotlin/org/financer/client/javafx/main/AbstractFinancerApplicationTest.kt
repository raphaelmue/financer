package org.financer.client.javafx.main

import com.jfoenix.controls.*
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import org.financer.client.domain.model.category.Category
import org.financer.client.domain.model.transaction.*
import org.financer.client.domain.model.user.Token
import org.financer.client.domain.model.user.User
import org.financer.client.domain.model.user.VerificationToken
import org.financer.client.format.I18N.get
import org.financer.client.javafx.components.DatePicker
import org.financer.client.javafx.components.DoubleField
import org.financer.client.javafx.components.IntegerField
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.shared.domain.model.value.objects.*
import org.financer.util.collections.TreeUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import java.time.LocalDate
import java.util.*
import java.util.concurrent.TimeoutException
import java.util.function.Predicate

abstract class AbstractFinancerApplicationTest : ApplicationTest() {
    var formatter: JavaFXFormatter? = null

    companion object {
        const val SHORT_SLEEP = 500
        const val MEDIUM_SLEEP = 1000
        private const val LONG_SLEEP = 5000

        init {
            System.setProperty("testfx.robot", "glass")
            System.setProperty("glass.platform", "Monocle")
            System.setProperty("monocle.platform", "Headless")
        }
    }

    protected fun user(): User {
        return User()
                .setId(1)
                .setEmail(Email("test@test.com"))
                .setName(Name("Test", "User"))
                .setPassword(HashedPassword(password()))
                .setTokens(HashSet(listOf(token())))
                .setVerified(false)
    }

    protected fun password(): String {
        return "password"
    }

    protected fun token(): Token {
        return Token()
                .setId(1)
                .setExpireDate(ExpireDate())
                .setIpAddress(IPAddress("192.168.0.1"))
                .setToken(tokenString())
                .setOperatingSystem(OperatingSystem(OperatingSystem.Values.LINUX))
    }

    protected fun verificationToken(): VerificationToken {
        return VerificationToken()
                .setId(1)
                .setExpireDate(ExpireDate(LocalDate.now().plusDays(15)))
                .setToken(tokenString())
    }

    protected fun tokenString(): TokenString {
        return TokenString("Z6XCS3tyyBlhPfsv7rMxLgfdyEOlUkv0CWSdbFYHCNX2wLlwpH97lJNP69Bny3XZ")
    }

    protected fun variableCategory(): Category {
        return Category()
                .setId(1)
                .setUser(user())
                .setCategoryClass(CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                .setName("Variable Category")
                .setParent(null)
    }

    protected fun variableCategoryParent(): Category {
        return Category()
                .setId(2)
                .setUser(user())
                .setCategoryClass(CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES))
                .setName("Variable Category Parent")
                .setParent(null)
    }

    protected fun fixedCategory(): Category {
        return Category()
                .setId(2)
                .setUser(user())
                .setCategoryClass(CategoryClass(CategoryClass.Values.FIXED_EXPENSES))
                .setName("Fixed Category")
                .setParent(null)
    }

    protected fun product(): Product {
        return Product()
                .setId(1)
                .setName("Test Product")
                .setQuantity(Quantity(2))
                .setAmount(Amount(50))
    }

    protected fun variableTransaction(): VariableTransaction {
        return VariableTransaction()
                .setId(1)
                .setValueDate(ValueDate())
                .setCategory(variableCategory())
                .setDescription("Test Purpose")
                .setVendor("Test Vendor")
                .addProduct(product())
    }

    protected fun attachment(): Attachment {
        return Attachment()
                .setId(1)
                .setName("test.pdf")
                .setTransaction(variableTransaction())
                .setUploadDate(LocalDate.now())
    }

    protected fun fixedTransactionAmount(): FixedTransactionAmount {
        return FixedTransactionAmount()
                .setId(1)
                .setAmount(Amount(50))
                .setValueDate(ValueDate())
    }

    protected fun fixedTransaction(): FixedTransaction {
        return FixedTransaction()
                .setId(2)
                .setCategory(fixedCategory())
                .setTimeRange(TimeRange())
                .setIsVariable(false)
                .setAmount(Amount(50.0))
                .setDay(1)
                .setDescription("Test Purpose")
                .setVendor("Test Vendor")
                .addFixedTransactionAmount(fixedTransactionAmount())
    }

    @BeforeEach
    @Throws(Exception::class)
    open fun setUpEach() {
    }

    fun <T : Node?> find(query: String?): T {
        return lookup(query).query()
    }

    fun <T : Node?> find(predicate: Predicate<T>?): T {
        return lookup(predicate).query()
    }

    fun register(user: User, password: String?) {
        clickOn(find<Node>("#openRegisterDialogBtn") as JFXButton)
        clickOn(find<Node>("#registerNameTextField") as JFXTextField)
        write(user.name!!.firstName)
        clickOn(find<Node>("#registerSurnameTextField") as JFXTextField)
        write(user.name!!.surname)
        clickOn(find<Node>("#registerEmailTextField") as JFXTextField)
        write(user.email!!.emailAddress)
        val birthDatePicker = find<DatePicker>("#registerBirthDatePicker")
        birthDatePicker.value = user.birthDate!!.birthDate
        clickOn(find<Node>("#genderComboBox") as JFXComboBox<*>)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        clickOn(find<Node>("#registerPasswordTextField") as JFXPasswordField)
        write(password)
        clickOn(find<Node>("#registerRepeatPasswordTextField") as JFXPasswordField)
        write(password)
        confirmDialog()
        sleep(LONG_SLEEP.toLong())
        formatter = JavaFXFormatter(LocalStorageImpl.getInstance())
    }

    fun login(user: User, password: String?) {
        clickOn(find<Node>("#loginEmailTextField") as TextField)
        write(user.email!!.emailAddress)
        clickOn(find<Node>("#loginPasswordField") as TextField)
        write(password)
        clickOn(find<Node>("#loginBtn") as Button)
    }

    @Throws(Exception::class)
    fun logout() {
        clickOn(formatter!!.format(user().name!!))
        clickOn("Logout")
    }

    fun addCategory(category: Category) {
        clickOn(find<Node>("#profileTabBtn") as JFXButton)
        sleep(MEDIUM_SLEEP.toLong())
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        val newCategoryBtn = find<Button>("#newCategoryBtn")
        clickOn(get(category.categoryClass!!.categoryClass.getName()))
        clickOn(newCategoryBtn)
        val categoryNameField = find<JFXTextField>("#inputDialogTextField")
        categoryNameField.text = ""
        clickOn(categoryNameField)
        write(category.name)
        confirmDialog()
        sleep(MEDIUM_SLEEP.toLong())
    }

    fun addVariableTransaction(transaction: VariableTransaction) {
        clickOn(find<Node>("#transactionsTabBtn") as JFXButton)
        sleep(MEDIUM_SLEEP.toLong())
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        clickOn(find<Node>("#newTransactionBtn") as JFXButton)
        val amountTextField = find<DoubleField>("#amountTextField")
        clickOn(amountTextField)
        eraseText(3)
        write(java.lang.Double.toString(transaction.amount.amount))
        clickOn(find<Node>("#categoryComboBox") as JFXComboBox<*>)
        press(KeyCode.DOWN).release(KeyCode.DOWN)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        clickOn(find<Node>("#purposeTextField") as JFXTextField)
        write(transaction.description)
        clickOn(find<Node>("#shopTextField") as JFXTextField)
        write(transaction.vendor)
        val valueDatePicker = find<DatePicker>("#valueDatePicker")
        valueDatePicker.value = transaction.valueDate!!.date
        confirmDialog()
        sleep(MEDIUM_SLEEP.toLong())
        transaction.category!!.setId((TreeUtil.getByValue(LocalStorageImpl.getInstance().readObject("categories"),
                transaction.category, Comparator.comparing(Category::name)) as Category).id)
    }

    fun addFixedTransaction(fixedTransaction: FixedTransaction) {
        clickOn(find<Node>("#transactionsTabBtn") as JFXButton)
        sleep(MEDIUM_SLEEP.toLong())
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        press(KeyCode.RIGHT).release(KeyCode.RIGHT)
        clickOn(find(Predicate { label: Label -> label.text.contains(fixedTransaction.category!!.name!!) }))
        clickOn(find<Node>("#newFixedTransactionBtn") as JFXButton)
        sleep(SHORT_SLEEP.toLong())
        clickOn(find<Node>("#dayTextField") as IntegerField)
        eraseText(1)
        write(Integer.toString(fixedTransaction.day))
        val datePicker = find<DatePicker>("#startDateDatePicker")
        datePicker.value = fixedTransaction.timeRange!!.startDate
        if (fixedTransaction.isVariable) {
            clickOn(find<Node>("#isVariableCheckbox") as JFXCheckBox)
            sleep(100)
            for (transactionAmount in fixedTransaction.getTransactionAmounts()) {
                clickOn(find<Node>("#newTransactionAmountBtn") as JFXButton)
                (find<Node>("#transactionAmountValueDatePicker") as DatePicker).value = transactionAmount.valueDate!!.date
                clickOn(find<Node>("#transactionAmountTextField") as DoubleField)
                eraseText(3)
                write(java.lang.Double.toString(transactionAmount.amount.amount))
                press(KeyCode.TAB).release(KeyCode.TAB)
                press(KeyCode.ENTER).release(KeyCode.ENTER)
            }
        } else {
            clickOn(find<Node>("#amountTextField") as DoubleField)
            eraseText(3)
            write(java.lang.Double.toString(fixedTransaction.amount.amount))
        }
        clickOn(find<Node>("#productTextField") as JFXTextField)
        write(fixedTransaction.product)
        clickOn(find<Node>("#purposeTextField") as JFXTextField)
        write(fixedTransaction.description)
        confirmDialog()
        sleep(SHORT_SLEEP.toLong())
    }

    val categoryTree: Category
        get() = variableCategory()

    fun confirmDialog() {
        clickOn("Ok")
    }

    @AfterEach
    @Throws(TimeoutException::class)
    open fun tearDownEach() {
        /* Close the window. It will be re-opened at the next test. */
        FxToolkit.hideStage()
        release(*arrayOf<KeyCode>())
        release(*arrayOf<MouseButton>())
    }
}
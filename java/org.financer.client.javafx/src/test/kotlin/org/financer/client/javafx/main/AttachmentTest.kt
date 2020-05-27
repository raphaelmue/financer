package org.financer.client.javafx.main

import com.jfoenix.controls.JFXButton
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import org.financer.util.RandomString
import org.junit.jupiter.api.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.concurrent.TimeoutException

@Tag("integration")
class AttachmentTest : AbstractFinancerApplicationTest() {
    @BeforeEach
    @Throws(Exception::class)
    fun setupEach() {
        super.setUpEach()
        val attachment = File(path)
        if (!attachment.parentFile.mkdirs() && !attachment.createNewFile()) {
            Assertions.fail<Any>("Attachment file or directories could not be created!")
        }
        BufferedWriter(FileWriter(attachment)).use { bufferedReader -> bufferedReader.write(content) }
    }

    private fun uploadAttachment() {
        register(user(), password())
        addCategory(variableCategory())
        addVariableTransaction(variableTransaction())
        clickOn(formatter!!.format(variableTransaction().valueDate!!))
        clickOn(find<Node>("#editTransactionBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn(find<Node>("#uploadAttachmentBtn") as JFXButton)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn("#pathTextField")
        write(path)
        press(KeyCode.TAB).release(KeyCode.TAB)
        press(KeyCode.TAB).release(KeyCode.TAB)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        Assertions.assertNotNull(clickOn("attachment.txt"))
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
    }

    @Test
    fun testUploadAttachment() {
        uploadAttachment()
        for (transaction in categoryTree.transactions) {
            Assertions.assertEquals(1, transaction.getAttachments().size)
        }
    }

    @Test
    fun testDeleteAttachment() {
        uploadAttachment()
        clickOn(find<Node>("#editTransactionBtn") as Button)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        clickOn("attachment.txt")
        clickOn(find<Node>("#deleteAttachmentBtn") as JFXButton)
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        press(KeyCode.TAB).release(KeyCode.TAB)
        press(KeyCode.TAB).release(KeyCode.TAB)
        press(KeyCode.TAB).release(KeyCode.TAB)
        press(KeyCode.TAB).release(KeyCode.TAB)
        press(KeyCode.ENTER).release(KeyCode.ENTER)
        sleep(AbstractFinancerApplicationTest.Companion.MEDIUM_SLEEP.toLong())
        confirmDialog()
        sleep(AbstractFinancerApplicationTest.Companion.SHORT_SLEEP.toLong())
        for (transaction in categoryTree.transactions) {
            Assertions.assertEquals(0, transaction.getAttachments().size)
        }
    }

    @AfterEach
    @Throws(TimeoutException::class)
    public override fun tearDownEach() {
        super.tearDownEach()
        val attachment = File(path)
        if (!attachment.delete()) {
            Assertions.fail<Any>("Attachment file could not be deleted!")
        }
    }

    companion object {
        private var content: String? = null
        private var path: String? = null

        @BeforeAll
        fun setup() {
            path = "/Financer/test/attachment.txt"
            path = if (System.getenv("APPDATA") != null) {
                System.getenv("APPDATA") + path
            } else {
                System.getProperty("user.home") + path
            }
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                path = path!!.replace("/", "\\")
            }
            val randomString = RandomString(1024)
            content = randomString.nextString()
        }
    }
}
package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.RandomString;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class AttachmentTest extends AbstractFinancerApplicationTest {

    private static String content;
    private static String path;

    @BeforeAll
    public static void setup() {
        path = "/Financer/test/attachment.txt";
        if (System.getenv("APPDATA") != null) {
            path = System.getenv("APPDATA") + path;
        } else {
            path = System.getProperty("user.home") + path;
        }

        RandomString randomString = new RandomString(1024);

        content = randomString.nextString();
    }

    @BeforeEach
    public void setupEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);

        File attachment = new File(path);
        if (!attachment.getParentFile().mkdirs() && !attachment.createNewFile()) {
            Assertions.fail("Attachment file or directories could not be created!");
        }
        try (BufferedWriter bufferedReader = new BufferedWriter(new FileWriter(attachment))) {
            bufferedReader.write(content);
        }
    }

    private void uploadAttachment() {
        register(this.user, this.password);
        addCategory(category);
        addTransaction(transaction);
        clickOn(transaction.getProduct());
        clickOn((Button) find("#editTransactionBtn"));
        sleep(SHORT_SLEEP);
        clickOn((JFXButton) find("#uploadAttachmentBtn"));
        sleep(SHORT_SLEEP);
        applyPath(path);
        sleep(MEDIUM_SLEEP);
        Assertions.assertNotNull(clickOn("attachment.txt"));
        confirmDialog();
        sleep(SHORT_SLEEP);
    }

    private void applyPath(String filePath) {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            filePath = filePath.replace("/", "\\");
        }
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(filePath);
        clipboard.setContents(stringSelection, stringSelection);
        press(KeyCode.CONTROL).press(KeyCode.V).release(KeyCode.V).release(KeyCode.CONTROL);
        push(KeyCode.ENTER);
    }

    @Test
    public void testUploadAttachment() {
        uploadAttachment();
        for (Transaction transaction : getCategoryTree().getTransactions()) {
            Assertions.assertEquals(1, transaction.getAttachments().size());
        }
    }

    @Test
    public void testDeleteAttachment() {
        uploadAttachment();
        clickOn((Button) find("#editTransactionBtn"));
        sleep(SHORT_SLEEP);
        clickOn("attachment.txt");
        clickOn((JFXButton) find("#deleteAttachmentBtn"));
        sleep(SHORT_SLEEP);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(MEDIUM_SLEEP);
        confirmDialog();
        sleep(SHORT_SLEEP);
        for (Transaction transaction : getCategoryTree().getTransactions()) {
            Assertions.assertEquals(0, transaction.getAttachments().size());
        }
    }

    @AfterEach
    public void tearDownEach() throws TimeoutException {
        super.tearDownEach();
        File attachment = new File(path);
        if (!attachment.delete()) {
            Assertions.fail("Attachment file could not be deleted!");
        }
    }
}

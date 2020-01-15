package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import de.raphaelmuesseler.financer.shared.model.transactions.Transaction;
import de.raphaelmuesseler.financer.util.RandomString;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            path = path.replace("/", "\\");
        }

        RandomString randomString = new RandomString(1024);

        content = randomString.nextString();
    }

    @BeforeEach
    public void setupEach() throws Exception {
        super.setUpEach();

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
        clickOn("#pathTextField");
        write(path);
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        sleep(MEDIUM_SLEEP);
        Assertions.assertNotNull(clickOn("attachment.txt"));
        confirmDialog();
        sleep(SHORT_SLEEP);
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
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.TAB).release(KeyCode.TAB);
        press(KeyCode.TAB).release(KeyCode.TAB);
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

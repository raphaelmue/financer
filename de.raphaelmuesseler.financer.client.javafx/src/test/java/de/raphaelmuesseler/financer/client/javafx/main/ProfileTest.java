package de.raphaelmuesseler.financer.client.javafx.main;

import com.jfoenix.controls.JFXPasswordField;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.Hash;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class ProfileTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
        ApplicationTest.launch(FinancerApplication.class);
    }

    @Test
    public void testChangePassword() {
        final String newPassword = "newPassword";

        register(user, password);

        clickOn((Button) find("#profileTabBtn"));
        sleep(SHORT_SLEEP);
        clickOn((Hyperlink) find("#changePasswordLink"));

        clickOn((JFXPasswordField) find("#oldPasswordField"));
        write(password);
        clickOn((JFXPasswordField) find("#newPasswordField"));
        write(newPassword);
        clickOn((JFXPasswordField) find("#repeatNewPasswordField"));
        write(newPassword);

        confirmDialog();

        sleep(SHORT_SLEEP);

        Assertions.assertEquals(Hash.create(newPassword, ((User)LocalStorageImpl.getInstance().readObject("user")).getSalt()),
                ((User)LocalStorageImpl.getInstance().readObject("user")).getPassword());
    }
}

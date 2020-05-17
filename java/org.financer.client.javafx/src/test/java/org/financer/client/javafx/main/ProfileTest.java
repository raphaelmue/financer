package org.financer.client.javafx.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.financer.client.domain.model.user.User;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.util.Hash;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@SuppressWarnings("WeakerAccess")
@Tag("integration")
public class ProfileTest extends AbstractFinancerApplicationTest {

    @BeforeEach
    public void setUpEach() throws Exception {
        super.setUpEach();
    }

    @Test
    public void testChangePassword() {
        final String newPassword = "newPassword";
        register(user(), password());

        clickOn((Button) find("#profileTabBtn"));
        sleep(SHORT_SLEEP);
        clickOn((Hyperlink) find("#changePasswordLink"));

        clickOn((JFXPasswordField) find("#oldPasswordField"));
        write(password());
        clickOn((JFXPasswordField) find("#newPasswordField"));
        write(newPassword);
        clickOn((JFXPasswordField) find("#repeatNewPasswordField"));
        write(newPassword);

        confirmDialog();

        sleep(SHORT_SLEEP);

        Assertions.assertEquals(Hash.create(newPassword, ((User) LocalStorageImpl.getInstance().readObject("user")).getPassword().getSalt()),
                ((User) LocalStorageImpl.getInstance().readObject("user")).getPassword().getHashedPassword());
    }

    @Test
    public void testChangePersonalInformation() {
        final String newName = "Peter";
        register(user(), password());

        clickOn((Button) find("#profileTabBtn"));
        sleep(SHORT_SLEEP);
        clickOn((JFXButton) find("#editPersonalInformationBtn"));
        sleep(SHORT_SLEEP);
        clickOn((JFXTextField) find("#nameField"));
        eraseText(formatter.format(user().getName()).length());
        write(newName);
        confirmDialog();
        sleep(SHORT_SLEEP);

        Assertions.assertEquals(newName, ((User) LocalStorageImpl.getInstance().readObject("user")).getName().getFirstName());
        Assertions.assertEquals(newName + " " + user().getName().getSurname(), ((Label) find("#fullNameLabel")).getText());
    }
}

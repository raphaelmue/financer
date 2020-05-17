package org.financer.client.javafx.main.profile;

import com.jfoenix.controls.JFXPasswordField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.I18N;
import org.financer.client.javafx.dialogs.FinancerDialog;
import org.financer.shared.domain.model.value.objects.HashedPassword;

class ChangePasswordDialog extends FinancerDialog<User> {
    private JFXPasswordField oldPasswordField;
    private JFXPasswordField newPasswordField;
    private JFXPasswordField repeatNewPasswordField;

    ChangePasswordDialog(User user) {
        super(user);

        this.setDialogTitle(I18N.get("password"));
    }

    @Override
    protected boolean checkConsistency() {
        final String oldPassword = oldPasswordField.getText();

        if (!getValue().getPassword().isEqualTo(oldPassword)) {
            this.setErrorMessage(I18N.get("errPasswordWrong"));
            return false;
        }

        if (!newPasswordField.getText().equals(repeatNewPasswordField.getText())) {
            this.setErrorMessage(I18N.get("errPasswordsDoNotMatch"));
            return false;
        }

        if (this.oldPasswordField.getText().equals(this.newPasswordField.getText())) {
            this.setErrorMessage(I18N.get("errPasswordsMustDistinguish"));
            return false;
        }

        return true;
    }

    @Override
    protected Region getDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(8);
        gridPane.setHgap(50);

        gridPane.add(new Label(I18N.get("oldPassword")), 0, 0);
        this.oldPasswordField = new JFXPasswordField();
        this.oldPasswordField.setId("oldPasswordField");
        gridPane.add(this.oldPasswordField, 1, 0);

        gridPane.add(new Label(I18N.get("newPassword")), 0, 1);
        this.newPasswordField = new JFXPasswordField();
        this.newPasswordField.setId("newPasswordField");
        gridPane.add(this.newPasswordField, 1, 1);

        gridPane.add(new Label(I18N.get("repeatNewPasswort")), 0, 2);
        this.repeatNewPasswordField = new JFXPasswordField();
        this.repeatNewPasswordField.setId("repeatNewPasswordField");
        gridPane.add(this.repeatNewPasswordField, 1, 2);

        return gridPane;
    }

    @Override
    protected void onConfirm() {
        getValue().setPassword(new HashedPassword(this.newPasswordField.getText()));
        super.onConfirm();
    }
}

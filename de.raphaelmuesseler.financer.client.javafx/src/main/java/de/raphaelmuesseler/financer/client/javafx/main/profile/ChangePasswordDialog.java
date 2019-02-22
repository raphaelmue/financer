package de.raphaelmuesseler.financer.client.javafx.main.profile;

import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

class ChangePasswordDialog extends FinancerDialog<User> {
    private PasswordField oldPasswordField, newPasswordField, repeatNewPasswordField;

    ChangePasswordDialog(User user) {
        super(user);

        this.setHeaderText(I18N.get("password"));

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    @Override
    protected boolean checkConsistency() {
        if (!Hash.create(oldPasswordField.getText(), this.getValue().getSalt()).equals(this.getValue().getPassword())) {
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
    protected Node setDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(8);
        gridPane.setHgap(50);

        gridPane.add(new Label(I18N.get("oldPassword")), 0, 0);
        this.oldPasswordField = new PasswordField();
        gridPane.add(this.oldPasswordField, 1, 0);

        gridPane.add(new Label(I18N.get("newPassword")), 0, 1);
        this.newPasswordField = new PasswordField();
        gridPane.add(this.newPasswordField, 1, 1);

        gridPane.add(new Label(I18N.get("repeatNewPasswort")), 0, 2);
        this.repeatNewPasswordField = new PasswordField();
        gridPane.add(this.repeatNewPasswordField, 1, 2);

        return gridPane;
    }

    @Override
    protected User onConfirm() {
        String salt = new RandomString(32).nextString();
        String hashedPassword = Hash.create(this.newPasswordField.getText(), salt);

        this.getValue().setPassword(hashedPassword);
        this.getValue().setSalt(salt);

        return super.onConfirm();
    }
}

package de.raphaelmuesseler.financer.client.javafx.login;

import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.User;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


public class RegisterDialog extends FinancerDialog<User> {
    private TextField nameField, surnameField, emailField;
    private PasswordField passwordField, passwordRepeatField;

    public RegisterDialog() {
        super(null);

        this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    @Override
    protected Node setDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(8);
        gridPane.setHgap(150);

        gridPane.add(new Label(I18N.get("name")), 0, 0);
        this.nameField = new TextField();
        gridPane.add(this.nameField, 1, 0);

        gridPane.add(new Label(I18N.get("surname")), 0, 1);
        this.surnameField = new TextField();
        gridPane.add(this.surnameField, 1, 1);

        gridPane.add(new Label(I18N.get("email")), 0, 2);
        this.emailField = new TextField();
        gridPane.add(this.emailField, 1, 2);

        gridPane.add(new Label(I18N.get("password")), 0, 3);
        this.passwordField = new PasswordField();
        gridPane.add(this.passwordField, 1, 3);

        gridPane.add(new Label(I18N.get("repeatPassword")), 0, 4);
        this.passwordRepeatField = new PasswordField();
        gridPane.add(this.passwordRepeatField, 1, 4);

        return gridPane;
    }

    @Override
    protected boolean checkConsistency() {
        boolean result = true;

        if (!this.passwordField.getText().equals(this.passwordRepeatField.getText())) {
            this.setErrorMessage(I18N.get("errPasswordsDoNotMatch"));
            result = false;
        }

        return result;
    }

    @Override
    protected User onConfirm() {
        String salt = new RandomString(32).nextString();
        String password = Hash.create(this.passwordField.getText(), salt);

        this.setValue(new User(this.emailField.getText(),
                password,
                salt,
                this.nameField.getText(),
                this.surnameField.getText()));

        return super.onConfirm();
    }
}

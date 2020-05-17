package org.financer.client.javafx.login;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.I18N;
import org.financer.client.javafx.components.DatePicker;
import org.financer.client.javafx.dialogs.FinancerDialog;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.domain.model.value.objects.*;

public class RegisterDialog extends FinancerDialog<User> {
    private JFXTextField nameField;
    private JFXTextField surnameField;
    private JFXTextField emailField;
    private JFXPasswordField passwordField;
    private JFXPasswordField passwordRepeatField;
    private DatePicker birthDatePicker;
    private JFXComboBox<Gender> genderComboBox;

    RegisterDialog() {
        super(null, ((LoginController) LoginController.getInstance()).getRootLayout());
        this.setDialogTitle(I18N.get("registration"));
    }

    @Override
    protected Region getDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(8);
        gridPane.setHgap(150);

        gridPane.add(new Label(I18N.get("name")), 0, 0);
        this.nameField = new JFXTextField();
        this.nameField.setId("registerNameTextField");
        gridPane.add(this.nameField, 1, 0);

        gridPane.add(new Label(I18N.get("surname")), 0, 1);
        this.surnameField = new JFXTextField();
        this.surnameField.setId("registerSurnameTextField");
        gridPane.add(this.surnameField, 1, 1);

        gridPane.add(new Label(I18N.get("email")), 0, 2);
        this.emailField = new JFXTextField();
        this.emailField.setId("registerEmailTextField");
        gridPane.add(this.emailField, 1, 2);

        gridPane.add(new Label(I18N.get("birthdate")), 0, 3);
        this.birthDatePicker = new DatePicker(new JavaFXFormatter(LocalStorageImpl.getInstance()));
        this.birthDatePicker.setId("registerBirthDatePicker");
        gridPane.add(this.birthDatePicker, 1, 3);

        gridPane.add(new Label(I18N.get("gender")), 0, 4);
        this.genderComboBox = new JFXComboBox<>();
        this.genderComboBox.setId("genderComboBox");
        this.genderComboBox.getItems().addAll(Gender.getAll());
        this.genderComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Gender gender) {
                return gender != null ? I18N.get(gender.getGender().getName()) : "";
            }

            @Override
            public Gender fromString(String name) {
                return new Gender(name);
            }
        });
        this.genderComboBox.setPlaceholder(new Label(I18N.get("gender")));
        gridPane.add(this.genderComboBox, 1, 4);

        gridPane.add(new Label(I18N.get("password")), 0, 5);
        this.passwordField = new JFXPasswordField();
        this.passwordField.setId("registerPasswordTextField");
        gridPane.add(this.passwordField, 1, 5);

        gridPane.add(new Label(I18N.get("repeatPassword")), 0, 6);
        this.passwordRepeatField = new JFXPasswordField();
        this.passwordRepeatField.setId("registerRepeatPasswordTextField");
        gridPane.add(this.passwordRepeatField, 1, 6);

        return gridPane;
    }

    @Override
    protected boolean checkConsistency() {
        boolean result = true;

        if (this.nameField.getText().isEmpty() || this.surnameField.getText().isEmpty() ||
                this.emailField.getText().isEmpty() || this.passwordField.getText().isEmpty() ||
                this.passwordRepeatField.getText().isEmpty()) {
            this.setErrorMessage(I18N.get("errFillRequiredFields"));
            result = false;
        }

        if (this.passwordField.getText().length() < 8) {
            this.setErrorMessage(I18N.get("errInvalidPasswordLength"));
            result = false;
        }

        if (!this.passwordField.getText().equals(this.passwordRepeatField.getText())) {
            this.setErrorMessage(I18N.get("errPasswordsDoNotMatch"));
            result = false;
        }

        return result;
    }

    @Override
    protected void onConfirm() {
        this.setValue(new User()
                .setId(0)
                .setEmail(new Email(this.emailField.getText()))
                .setPassword(new HashedPassword(this.passwordField.getText()))
                .setName(new Name(this.nameField.getText(), this.surnameField.getText()))
                .setBirthDate(new BirthDate(this.birthDatePicker.getValue()))
                .setGender(this.genderComboBox.getSelectionModel().getSelectedItem()));

        super.onConfirm();
    }
}

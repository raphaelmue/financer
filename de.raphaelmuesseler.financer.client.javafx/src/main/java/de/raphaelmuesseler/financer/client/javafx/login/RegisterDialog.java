package de.raphaelmuesseler.financer.client.javafx.login;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.user.User;
import de.raphaelmuesseler.financer.util.Hash;
import de.raphaelmuesseler.financer.util.RandomString;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;


public class RegisterDialog extends FinancerDialog<User> {
    private TextField nameField, surnameField, emailField;
    private PasswordField passwordField, passwordRepeatField;
    private JFXDatePicker birthDatePicker;
    private ComboBox<User.Gender> genderComboBox;

    RegisterDialog() {
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
        this.nameField.setId("registerNameTextField");
        gridPane.add(this.nameField, 1, 0);

        gridPane.add(new Label(I18N.get("surname")), 0, 1);
        this.surnameField = new TextField();
        this.surnameField.setId("registerSurnameTextField");
        gridPane.add(this.surnameField, 1, 1);

        gridPane.add(new Label(I18N.get("email")), 0, 2);
        this.emailField = new TextField();
        this.emailField.setId("registerEmailTextField");
        gridPane.add(this.emailField, 1, 2);

        gridPane.add(new Label(I18N.get("birthdate")), 0, 3);
        this.birthDatePicker = new JFXDatePicker();
        this.birthDatePicker.setId("registerBirthDatePicker");
        gridPane.add(this.birthDatePicker, 1, 3);

        gridPane.add(new Label(I18N.get("gender")), 0, 4);
        this.genderComboBox = new ComboBox<>();
        this.genderComboBox.setId("genderComboBox");
        this.genderComboBox.getItems().addAll(User.Gender.values());
//        ListCell<User.Gender> listCell = new ListCell<>() {
////            @Override
////            protected void updateItem(User.Gender item, boolean empty) {
////                super.updateItem(item, empty);
////
////                if (!empty && item != null) {
////                    setText(I18N.get(item.getName()));
////                } else {
////                    setText(null);
////                }
////            }
////        };
////        this.genderComboBox.setCellFactory(param -> listCell);
        this.genderComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User.Gender gender) {
                return gender != null ? I18N.get(gender.getName()) : "";
            }

            @Override
            public User.Gender fromString(String name) {
                return User.Gender.getGenderByName(name);
            }
        });
        this.genderComboBox.setPlaceholder(new Label(I18N.get("gender")));
        gridPane.add(this.genderComboBox, 1, 4);

        gridPane.add(new Label(I18N.get("password")), 0, 5);
        this.passwordField = new PasswordField();
        this.passwordField.setId("registerPasswordTextField");
        gridPane.add(this.passwordField, 1, 5);

        gridPane.add(new Label(I18N.get("repeatPassword")), 0, 6);
        this.passwordRepeatField = new PasswordField();
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
    protected User onConfirm() {
        String salt = new RandomString(32).nextString();
        String password = Hash.create(this.passwordField.getText(), salt);

        this.setValue(new User(0,
                this.emailField.getText(),
                password,
                salt,
                this.nameField.getText(),
                this.surnameField.getText(),
                this.birthDatePicker.getValue(),
                this.genderComboBox.getSelectionModel().getSelectedItem()));

        return super.onConfirm();
    }
}

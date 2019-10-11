package de.raphaelmuesseler.financer.client.javafx.main.profile;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.javafx.dialogs.FinancerDialog;
import de.raphaelmuesseler.financer.shared.model.user.User;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

public class ChangePersonalInformationDialog extends FinancerDialog<User> {

    private JFXTextField nameField;
    private JFXTextField surnameField;
    private JFXDatePicker birthDatePicker;
    private JFXComboBox<User.Gender> genderComboBox;

    public ChangePersonalInformationDialog(User value) {
        super(value);

        this.setDialogTitle(I18N.get("personalInformation"));
        this.prepareDialogContent();
    }

    @Override
    protected void prepareDialogContent() {
        this.nameField.setText(this.getValue().getName());
        this.surnameField.setText(this.getValue().getSurname());
        this.birthDatePicker.setValue(this.getValue().getBirthDate());
        this.genderComboBox.setValue(this.getValue().getGender());
    }

    @Override
    protected Region getDialogContent() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(80);
        gridPane.setVgap(8);

        gridPane.add(new Label(I18N.get("name")), 0, 0);
        this.nameField = new JFXTextField();
        this.nameField.setId("nameField");
        gridPane.add(this.nameField, 1, 0);

        gridPane.add(new Label(I18N.get("surname")), 0, 1);
        this.surnameField = new JFXTextField();
        this.surnameField.setId("surnameField");
        gridPane.add(this.surnameField, 1, 1);

        gridPane.add(new Label(I18N.get("birthdate")), 0, 2);
        this.birthDatePicker = new JFXDatePicker();
        this.birthDatePicker.setId("registerBirthDatePicker");
        gridPane.add(this.birthDatePicker, 1, 2);

        gridPane.add(new Label(I18N.get("gender")), 0, 3);
        this.genderComboBox = new JFXComboBox<>();
        this.genderComboBox.setId("genderComboBox");
        this.genderComboBox.getItems().addAll(User.Gender.values());
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
        gridPane.add(this.genderComboBox, 1, 3);

        return gridPane;
    }

    @Override
    protected boolean checkConsistency() {
        boolean result = true;

        if (this.nameField.getText().isEmpty() || this.surnameField.getText().isEmpty() || this.birthDatePicker.getValue() == null) {
            this.setErrorMessage(I18N.get("errFillRequiredFields"));
            result = false;
        }
        return result;
    }

    @Override
    protected void onConfirm() {
        this.getValue().setName(this.nameField.getText());
        this.getValue().setSurname(this.surnameField.getText());
        this.getValue().setBirthDate(this.birthDatePicker.getValue());
        this.getValue().setGender(this.genderComboBox.getValue());

        super.onConfirm();
    }
}

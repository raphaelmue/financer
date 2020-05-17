package org.financer.client.javafx.main.profile;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.I18N;
import org.financer.client.javafx.dialogs.FinancerDialog;
import org.financer.shared.domain.model.value.objects.BirthDate;
import org.financer.shared.domain.model.value.objects.Gender;
import org.financer.shared.domain.model.value.objects.Name;

public class ChangePersonalInformationDialog extends FinancerDialog<User> {

    private JFXTextField nameField;
    private JFXTextField surnameField;
    private JFXDatePicker birthDatePicker;
    private JFXComboBox<Gender> genderComboBox;

    public ChangePersonalInformationDialog(User value) {
        super(value);

        this.setDialogTitle(I18N.get("personalInformation"));
        this.prepareDialogContent();
    }

    @Override
    protected void prepareDialogContent() {
        this.nameField.setText(this.getValue().getName().getFirstName());
        this.surnameField.setText(this.getValue().getName().getSurname());
        this.birthDatePicker.setValue(this.getValue().getBirthDate().getBirthDate());
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
        this.getValue().setName(new Name(this.nameField.getText(), this.surnameField.getText()));
        this.getValue().setBirthDate(new BirthDate(this.birthDatePicker.getValue()));
        this.getValue().setGender(this.genderComboBox.getValue());

        super.onConfirm();
    }
}

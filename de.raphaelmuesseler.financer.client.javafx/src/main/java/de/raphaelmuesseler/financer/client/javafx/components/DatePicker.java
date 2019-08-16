package de.raphaelmuesseler.financer.client.javafx.components;

import com.jfoenix.controls.JFXDatePicker;
import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.javafx.format.JavaFXFormatter;
import de.raphaelmuesseler.financer.client.javafx.local.LocalStorageImpl;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class DatePicker extends JFXDatePicker {

    public DatePicker() {
        this(new JavaFXFormatter(LocalStorageImpl.getInstance()));
    }

    public DatePicker(Formatter formatter) {
        super();

        this.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) {
                    return "";
                } else {
                    return formatter.formatDate(localDate);
                }
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                } else {
                    return formatter.convertStringToLocalDate(dateString);
                }
            }
        });
    }
}

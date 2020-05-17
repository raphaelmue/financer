package org.financer.client.javafx.components;

import com.jfoenix.controls.JFXDatePicker;
import javafx.util.StringConverter;
import org.financer.client.format.Formatter;
import org.financer.client.javafx.format.JavaFXFormatter;
import org.financer.client.javafx.local.LocalStorageImpl;
import org.financer.shared.domain.model.Settings;
import org.financer.shared.domain.model.value.objects.SettingPair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

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
                    return formatter.format(localDate);
                }
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                } else {
                    Locale locale = ((Settings) LocalStorageImpl.getInstance().readObject("user")).getValueOrDefault(SettingPair.Property.LANGUAGE);
                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
                    return LocalDate.parse(dateString, formatter);
                }
            }
        });
    }
}

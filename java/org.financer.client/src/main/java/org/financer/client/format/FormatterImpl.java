package org.financer.client.format;

import org.financer.client.domain.model.user.User;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.Formattable;
import org.financer.shared.domain.model.value.objects.SettingPair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class FormatterImpl implements Formatter {
    protected final User user;

    public FormatterImpl(LocalStorage localStorage) {
        this.user = localStorage.readObject("user");
    }

    public FormatterImpl(User user) {
        this.user = user;
    }

    @Override
    public String format(Formattable formattable) {
        return formattable.format(user);
    }

    @Override
    public String format(LocalDate localDate) {
        Locale locale = user.getValueOrDefault(SettingPair.Property.LANGUAGE);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        return localDate.format(formatter);
    }

    public String format(Double value) {
        Locale locale = user.getValueOrDefault(SettingPair.Property.LANGUAGE);
        return String.format(locale, "%.2f", value) + " ";
    }
}

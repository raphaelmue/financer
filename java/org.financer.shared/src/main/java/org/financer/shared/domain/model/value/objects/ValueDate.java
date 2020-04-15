package org.financer.shared.domain.model.value.objects;

import org.financer.shared.domain.model.Formattable;
import org.financer.shared.domain.model.Settings;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;

@Embeddable
@Immutable
public class ValueDate implements Serializable, Formattable {
    private static final long serialVersionUID = -1612116802619061353L;

    @Column(name = "value_date", nullable = false)
    private final LocalDate date;

    /**
     * Instantiates the value date with current date.
     */
    public ValueDate() {
        this.date = LocalDate.now();
    }

    public ValueDate(LocalDate date) {
        this.date = date;
    }

    public ValueDate(String dateString) {
        this.date = LocalDate.parse(dateString);
    }

    /**
     * Checks whether month and year of both value dates are equal.
     *
     * @param valueDate value date to compare to
     * @return true if month and year are equal
     */
    public boolean isInSameMonth(ValueDate valueDate) {
        return (this.date.getYear() == valueDate.getDate().getYear() &&
                this.date.getMonthValue() == valueDate.getDate().getMonthValue());
    }

    /**
     * Returns the month difference between this value date and the given value date.
     *
     * @param valueDate value date to compare to
     * @return month difference
     */
    public int getMonthDifference(ValueDate valueDate) {
        return (12 * Period.between(this.getDate().withDayOfMonth(1), valueDate.getDate().withDayOfMonth(1)).getYears()) +
                Period.between(this.getDate().withDayOfMonth(1), valueDate.getDate().withDayOfMonth(1)).getMonths();
    }

    @Override
    public String format(Settings settings) {
        Locale locale = settings.getValue(SettingPair.Property.LANGUAGE);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
        return this.getDate().format(formatter);
    }
    /*
     * Getters and Setters
     */

    public LocalDate getDate() {
        return date;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        return date.equals(((ValueDate) object).date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return "ValueDate [" +
                "date=" + date +
                ']';
    }
}

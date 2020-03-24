package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Embeddable
@Immutable
public class ValueDate {

    @Column(name = "value_date")
    private final LocalDate date;

    public ValueDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Instantiates the value date with current date.
     */
    public ValueDate() {
        this.date = LocalDate.now();
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
}
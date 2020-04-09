package org.financer.shared.domain.model.value.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Class that specifies a time range with a start and an end date. This time range can also be open by setting the end
 * date to null.
 */
@Embeddable
@Immutable
public class TimeRange implements Serializable {
    private static final long serialVersionUID = 3710079875858283394L;

    @Column(name = "start_date", nullable = false)
    @NotNull
    private final LocalDate startDate;

    @Column(name = "end_date")
    private final LocalDate endDate;

    public TimeRange() {
        this(LocalDate.now());
    }

    public TimeRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TimeRange(LocalDate startDate) {
        this.startDate = startDate;
        this.endDate = null;
    }

    /**
     * Checks whether the current date is in between the time range or not. If the time range has an open end, it
     * returns true.
     *
     * @return true if the current date is in between the time range, false otherwise
     */
    public boolean includes() {
        return this.includes(new ValueDate());
    }

    /**
     * Checks whether a given date is in between the time range or not. If the time range has an open end, it returns
     * true.
     *
     * @param valueDate date to check
     * @return true if the given date is in between the time range, false otherwise
     */
    public boolean includes(ValueDate valueDate) {
        return ((!this.startDate.isAfter(valueDate.getDate())) &&
                (this.endDate == null || !this.endDate.isBefore(valueDate.getDate())));
    }

    /**
     * Returns the intersection of months between this time range and the give time range. If both time ranges are
     * disjoint, it returns null.
     *
     * @param timeRange time range to calculate intersection with
     * @return intersection of months
     */
    public TimeRange getMonthIntersection(TimeRange timeRange) {
        // if both time ranges are disjoint, return 0
        if (this.getStartDate().isAfter(timeRange.getEndDate()) && this.getEndDate().isBefore(timeRange.getStartDate())) {
            return null;
        }

        ValueDate maxStartDate;
        ValueDate minEndDate;

        if (this.getEndDate() == null) {
            minEndDate = new ValueDate(timeRange.getEndDate());
        } else {
            if (timeRange.getEndDate().isAfter(this.endDate)) {
                minEndDate = new ValueDate(this.endDate);
            } else {
                minEndDate = new ValueDate(timeRange.getEndDate());
            }
        }

        if (timeRange.getStartDate().isBefore(this.startDate)) {
            maxStartDate = new ValueDate(this.startDate);
        } else {
            maxStartDate = new ValueDate(timeRange.getStartDate());
        }

        return new TimeRange(maxStartDate.getDate(), minEndDate.getDate());
    }

    /**
     * Returns the difference of months between start and end date. If end date is null, Integer.MAX_VALUE is returned.
     *
     * @return month difference
     */
    @JsonIgnore
    public int getMonthDifference() {
        if (this.endDate == null) {
            return Integer.MAX_VALUE;
        }
        return new ValueDate(this.startDate).getMonthDifference(new ValueDate(this.endDate));
    }

    /*
     * Getters and Setters
     */

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public TimeRange setEndDate(LocalDate endDate) {
        return new TimeRange(this.startDate, endDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeRange timeRange = (TimeRange) o;
        return Objects.equals(startDate, timeRange.startDate) &&
                Objects.equals(endDate, timeRange.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        return "TimeRange [" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ']';
    }
}

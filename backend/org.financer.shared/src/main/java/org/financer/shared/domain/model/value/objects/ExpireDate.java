package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@Immutable
public class ExpireDate implements Serializable {
    private static final long serialVersionUID = -6031939301023199834L;


    @Column(name = "expire_date", nullable = false)
    private final LocalDate expireDate;

    public ExpireDate() {
        this.expireDate = LocalDate.now().plusMonths(1);
    }

    public ExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    /**
     * Indicates whether this expire date is valid, i.e. whether the expire date is after or equal the current date.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return !this.expireDate.isBefore(LocalDate.now());
    }

    /**
     * Extends the expire date by one month from now. It does not change the expire date as this class is immutable.
     *
     * @return expire date object
     */
    public ExpireDate update() {
        return new ExpireDate(LocalDate.now().plusMonths(1));
    }

    /*
     * Getters and Setters
     */

    public LocalDate getExpireDate() {
        return expireDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpireDate that = (ExpireDate) o;
        return Objects.equals(expireDate, that.expireDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expireDate);
    }

    @Override
    public String toString() {
        return "ExpireDate [" +
                "expireDate=" + expireDate +
                ']';
    }
}
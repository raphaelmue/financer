package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@Immutable
public class ExpireDate {

    @Column(name = "expire_date")
    private final LocalDate expireDate;

    public ExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public ExpireDate() {
        this.expireDate = LocalDate.now();
    }

    /**
     * Indicates whether this expire date is valid, i.e. whether the expire date is before or equal the current date.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return !this.expireDate.isAfter(LocalDate.now());
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
}

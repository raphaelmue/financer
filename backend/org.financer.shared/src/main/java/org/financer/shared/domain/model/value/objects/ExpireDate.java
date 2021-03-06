package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.shared.domain.model.Expireable;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for expire date")
public class ExpireDate implements Serializable, Expireable {
    private static final long serialVersionUID = -6031939301023199834L;

    @EqualsAndHashCode.Include
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
    @Override
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

}

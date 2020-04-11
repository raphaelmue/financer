package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
@Immutable
public class Quantity {

    @Column(name = "quantity", nullable = false)
    private final int numberOfItems;

    public Quantity() {
        this.numberOfItems = 1;
    }

    public Quantity(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return numberOfItems == quantity.numberOfItems;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfItems);
    }

    @Override
    public String toString() {
        return "Quantity [" +
                "numberOfItems=" + numberOfItems +
                ']';
    }
}

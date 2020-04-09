package org.financer.shared.domain.model.value.objects;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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
}

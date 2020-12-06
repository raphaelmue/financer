package org.financer.shared.domain.model.value.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for quantity")
public class Quantity {

    @EqualsAndHashCode.Include
    @Column(name = "quantity", nullable = false)
    @Schema(description = "Quantity", required = true, example = "2")
    private final int numberOfItems;

    public Quantity() {
        this.numberOfItems = 1;
    }

    public Quantity(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
}

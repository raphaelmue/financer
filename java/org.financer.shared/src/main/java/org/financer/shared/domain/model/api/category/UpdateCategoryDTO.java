package org.financer.shared.domain.model.api.category;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class UpdateCategoryDTO extends CreateCategoryDTO {

    @NotNull
    @Min(1)
    private long id;

    public long getId() {
        return id;
    }

    public UpdateCategoryDTO setId(long id) {
        this.id = id;
        return this;
    }
}

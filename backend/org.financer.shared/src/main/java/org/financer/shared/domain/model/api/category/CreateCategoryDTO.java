package org.financer.shared.domain.model.api.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.CategoryClass;

import javax.validation.constraints.NotNull;

/**
 * DTO for creating a new category.
 */
@Data
@Accessors(chain = true)
@Schema(name = "CreateCategory", description = "Schema for creating a new category")
public class CreateCategoryDTO implements DataTransferObject {

    @NotNull
    @Schema(description = "Name of the category", required = true, example = "Food")
    private String name;

    @NotNull
    @Schema(description = "Name of the category class", required = true, example = "FIXED_EXPENSES")
    private CategoryClass.Values categoryClass;

    @Schema(description = "Id of the parent category")
    private Long parentId;
}

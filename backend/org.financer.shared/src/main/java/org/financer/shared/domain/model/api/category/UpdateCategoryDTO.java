package org.financer.shared.domain.model.api.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.CategoryClass;

@Data
@Accessors(chain = true)
@Schema(name = "UpdateCategory", description = "Schema for updating a category")
public class UpdateCategoryDTO implements DataTransferObject {

    @Schema(description = "Name of the category", example = "Food")
    private String name = null;

    @Schema(description = "Name of the category class", example = "FIXED_EXPENSES")
    private CategoryClass.Values categoryClass = null;

    @Schema(description = "Id of the parent category", example = "3")
    private long parentId = -1;
}

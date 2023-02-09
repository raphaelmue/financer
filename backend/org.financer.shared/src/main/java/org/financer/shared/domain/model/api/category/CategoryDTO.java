package org.financer.shared.domain.model.api.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.CategoryClass;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Data
@Accessors(chain = true)
@Schema(name = "Category", description = "Schema for a category")
public class CategoryDTO implements DataTransferObject {

    @NotNull
    @Size(min = 1)
    @Schema(description = "Identifier of the category", required = true, minimum = "1")
    private long id;

    @NotNull
    @Schema(description = "Category class of the category", required = true, enumAsRef = true)
    private CategoryClass.Values categoryClass;

    @Schema(description = "Id of the parent category")
    private Long parentId;

    @NotNull
    @Schema(description = "Name of the category", required = true, example = "Food")
    private String name;

    @Schema(description = "List of the child categories")
    private Set<CategoryDTO> children;
}

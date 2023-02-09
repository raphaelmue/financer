package org.financer.shared.domain.model.api.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.financer.shared.domain.model.api.DataTransferObject;

import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Schema(name = "CreateAttachment", description = "Schema for creating a new attachment")
public class CreateAttachmentDTO implements DataTransferObject {

    @NotNull
    @Schema(description = "Name of the attachment", example = "file.pdf", required = true)
    private String name;

    @NotNull
    @Schema(description = "Content of the attachment", required = true, type = "string", format = "binary")
    private byte[] content;

}

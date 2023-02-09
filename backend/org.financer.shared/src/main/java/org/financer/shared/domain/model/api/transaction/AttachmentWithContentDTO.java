package org.financer.shared.domain.model.api.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
@Schema(name = "AttachmentWithContent", description = "Schema for an attachment with content")
public class AttachmentWithContentDTO extends AttachmentDTO {

    @NotNull
    @Schema(description = "Name of the attachment", example = "file.pdf", required = true, type = "string", format = "binary")
    private byte[] content;

}

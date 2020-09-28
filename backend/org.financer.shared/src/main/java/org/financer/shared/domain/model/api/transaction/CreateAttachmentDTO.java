package org.financer.shared.domain.model.api.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;

import javax.validation.constraints.NotNull;

@Schema(name = "CreateAttachment", description = "Schema for creating a new attachment")
public class CreateAttachmentDTO implements DataTransferObject {

    @NotNull
    @Schema(description = "Name of the attachment", example = "file.pdf", required = true)
    private String name;

    @NotNull
    @Schema(description = "Content of the attachment", required = true)
    private byte[] content;

    public String getName() {
        return name;
    }

    public CreateAttachmentDTO setName(String name) {
        this.name = name;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public CreateAttachmentDTO setContent(byte[] content) {
        this.content = content;
        return this;
    }
}

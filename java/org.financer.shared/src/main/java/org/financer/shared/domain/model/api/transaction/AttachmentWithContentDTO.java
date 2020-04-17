package org.financer.shared.domain.model.api.transaction;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(name = "AttachmentWithContent", description = "Schema for an attachment with content")
public class AttachmentWithContentDTO extends AttachmentDTO {

    @NotNull
    @Schema(description = "Name of the attachment", example = "file.pdf", required = true)
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public AttachmentWithContentDTO setContent(byte[] content) {
        this.content = content;
        return this;
    }
}

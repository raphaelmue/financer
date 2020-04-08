package org.financer.shared.domain.model.api.transaction;

import javax.validation.constraints.NotNull;

public class AttachmentWithContentDTO extends AttachmentDTO {

    @NotNull
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public AttachmentWithContentDTO setContent(byte[] content) {
        this.content = content;
        return this;
    }
}

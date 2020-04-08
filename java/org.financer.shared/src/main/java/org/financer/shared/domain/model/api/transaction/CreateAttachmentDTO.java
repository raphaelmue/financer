package org.financer.shared.domain.model.api.transaction;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class CreateAttachmentDTO {

    @NotNull
    private String name;

    @NotNull
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

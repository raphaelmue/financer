package org.financer.shared.domain.model.api.transaction;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class AttachmentDTO {

    @NotNull
    @Min(1)
    private int id;

    private String name;

    private LocalDate uploadDate;

    private byte[] content;

    public int getId() {
        return id;
    }

    public AttachmentDTO setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AttachmentDTO setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public AttachmentDTO setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public AttachmentDTO setContent(byte[] content) {
        this.content = content;
        return this;
    }
}

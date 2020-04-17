package org.financer.shared.domain.model.api.transaction;


import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Schema(name = "Attachment", description = "Schema for an attachment")
public class AttachmentDTO {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the attachment", required = true, minimum = "1")
    private int id;

    @NotNull
    @Schema(description = "Name of the attachment", example = "file.pdf", required = true)
    private String name;

    @NotNull
    @PastOrPresent
    @Schema(description = "Upload date of the attachment", example = "2020-01-01", required = true)
    private LocalDate uploadDate;

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

}

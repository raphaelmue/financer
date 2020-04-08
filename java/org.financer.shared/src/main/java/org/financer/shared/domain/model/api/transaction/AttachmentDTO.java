package org.financer.shared.domain.model.api.transaction;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public class AttachmentDTO {

    @NotNull
    @Min(1)
    private int id;

    @NotNull
    private String name;

    @NotNull
    @PastOrPresent
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

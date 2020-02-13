package org.financer.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public class AttachmentDTO {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @JsonProperty("name")
    @ApiModelProperty(value = "File name", required = true, example = "file.txt")
    private String name;

    @JsonProperty("uploadDate")
    @ApiModelProperty(value = "Upload Date", required = true, example = "2020-02-02")
    private LocalDate uploadDate;

    @JsonProperty("content")
    @ApiModelProperty(value = "Content", required = true)
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

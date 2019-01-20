package de.raphaelmuesseler.financer.shared.model.db;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDate;

public class Attachment implements DatabaseObject, Serializable {
    private static final long serialVersionUID = 5087900373125640764L;

    public Attachment() { }

    private int id;
    private String name;
    @SerializedName("upload_date")
    private String uploadDate;
    private transient byte[] content;

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getUploadDate() {
        return LocalDate.parse(uploadDate);
    }

    public byte[] getContent() {
        return content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate.toString();
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

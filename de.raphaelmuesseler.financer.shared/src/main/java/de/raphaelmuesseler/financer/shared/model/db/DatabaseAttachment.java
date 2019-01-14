package de.raphaelmuesseler.financer.shared.model.db;

import java.io.Serializable;

public class DatabaseAttachment implements DatabaseObject, Serializable {
    private static final long serialVersionUID = 5087900373125640764L;

    public DatabaseAttachment() { }

    private int id;
    private String name;
    private byte[] content;

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setContent(byte[] content) {
        this.content = content;
    }
}

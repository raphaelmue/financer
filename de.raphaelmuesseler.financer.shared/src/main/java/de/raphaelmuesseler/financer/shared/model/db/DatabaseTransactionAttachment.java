package de.raphaelmuesseler.financer.shared.model.db;

import java.time.LocalDate;

public class DatabaseTransactionAttachment {
    private int id;
    private DatabaseTransaction transaction;
    private String name;
    private LocalDate uploadDate;
    private byte[] content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DatabaseTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(DatabaseTransaction transaction) {
        this.transaction = transaction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

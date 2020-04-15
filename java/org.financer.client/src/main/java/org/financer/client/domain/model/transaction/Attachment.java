package org.financer.client.domain.model.transaction;

import java.time.LocalDate;

public class Attachment {

    private long id;
    private Transaction transaction;
    private String name;
    private LocalDate uploadDate;
    private byte[] content;

    /*
     * Getters and Setters
     */

    public long getId() {
        return id;
    }

    public Attachment setId(long id) {
        this.id = id;
        return this;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Attachment setTransaction(Transaction transaction) {
        this.transaction = transaction;
        return this;
    }

    public String getName() {
        return name;
    }

    public Attachment setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public Attachment setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Attachment setContent(byte[] content) {
        this.content = content;
        return this;
    }
}

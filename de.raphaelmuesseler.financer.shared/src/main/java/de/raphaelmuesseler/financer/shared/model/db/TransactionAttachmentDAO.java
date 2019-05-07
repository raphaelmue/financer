package de.raphaelmuesseler.financer.shared.model.db;

import java.sql.Blob;
import java.time.LocalDate;

public class TransactionAttachmentDAO implements DataAccessObject {
    private final static long serialVersionUID = 7758316425770345150L;

    private int id;
    private VariableTransactionDAO transaction;
    private String name;
    private LocalDate uploadDate;
    private Blob content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VariableTransactionDAO getTransaction() {
        return transaction;
    }

    public void setTransaction(VariableTransactionDAO transaction) {
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

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }
}

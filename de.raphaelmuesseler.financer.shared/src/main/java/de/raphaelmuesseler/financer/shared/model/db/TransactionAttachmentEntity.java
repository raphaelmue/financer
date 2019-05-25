package de.raphaelmuesseler.financer.shared.model.db;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transactions_attachments")
public class TransactionAttachmentEntity implements DataEntity {
    private final static long serialVersionUID = 7758316425770345150L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne()
    private VariableTransactionEntity transaction;

    @Column(name = "name")
    private String name;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

    @Column(name = "content")
    @Lob
    private byte[] content;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VariableTransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(VariableTransactionEntity transaction) {
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

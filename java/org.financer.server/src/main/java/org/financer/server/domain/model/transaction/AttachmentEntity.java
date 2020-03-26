package org.financer.server.domain.model.transaction;

import org.financer.server.domain.model.DataEntity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class AttachmentEntity implements DataEntity {
    private static final long serialVersionUID = 7758316425770345150L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(targetEntity = TransactionEntity.class, optional = false)
    private TransactionEntity transaction;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "content", nullable = false)
    @Lob
    private byte[] content;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionEntity transaction) {
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

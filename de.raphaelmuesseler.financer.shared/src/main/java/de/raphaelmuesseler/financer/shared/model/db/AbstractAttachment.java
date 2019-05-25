package de.raphaelmuesseler.financer.shared.model.db;

import javax.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
public abstract class AbstractAttachment implements DataEntity {

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
}

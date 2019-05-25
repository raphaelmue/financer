package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.db.TransactionAttachmentEntity;

import javax.sql.rowset.serial.SerialBlob;
import java.io.Serializable;
import java.sql.SQLException;

public class Attachment extends TransactionAttachmentEntity implements Serializable {
    private static final long serialVersionUID = 5087900373125640764L;

    public Attachment() {
        super();
    }

    public Attachment(TransactionAttachmentEntity databaseAttachment) {
        this.setId(databaseAttachment.getId());
        this.setTransaction(databaseAttachment.getTransaction());
        this.setName(databaseAttachment.getName());
        this.setUploadDate(databaseAttachment.getUploadDate());
        this.setContent(databaseAttachment.getContent());
    }

    @Override
    public TransactionAttachmentEntity toEntity() {
        TransactionAttachmentEntity databaseAttachment = new TransactionAttachmentEntity();
        databaseAttachment.setId(this.getId());
        databaseAttachment.setTransaction(this.getTransaction());
        databaseAttachment.setName(this.getName());
        databaseAttachment.setContent(this.getContent());
        databaseAttachment.setUploadDate(this.getUploadDate());
        return databaseAttachment;
    }
}

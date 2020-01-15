package org.financer.shared.model.transactions;

import org.financer.shared.model.db.AttachmentEntity;

import java.io.Serializable;
import java.time.LocalDate;

public class Attachment extends AttachmentEntity implements Serializable {
    private static final long serialVersionUID = 5087900373125640764L;

    public Attachment() {
        super();
    }

    public Attachment(AttachmentEntity attachmentEntity) {
        this.setId(attachmentEntity.getId());
        this.setTransaction(attachmentEntity.getTransaction());
        this.setName(attachmentEntity.getName());
        this.setUploadDate(attachmentEntity.getUploadDate());
    }

    public Attachment(int id, VariableTransaction variableTransaction, String name, LocalDate uploadDate) {
        this.setId(id);
        this.setTransaction(variableTransaction);
        this.setName(name);
        this.setUploadDate(uploadDate);
    }

    @Override
    public AttachmentEntity toEntity() {
        AttachmentEntity databaseAttachment = new AttachmentEntity();
        databaseAttachment.setId(this.getId());
        databaseAttachment.setTransaction(this.getTransaction());
        databaseAttachment.setName(this.getName());
        databaseAttachment.setUploadDate(this.getUploadDate());
        return databaseAttachment;
    }
}

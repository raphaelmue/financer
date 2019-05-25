package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.db.ContentAttachmentEntity;

import java.io.Serializable;

public class ContentAttachment extends ContentAttachmentEntity implements Serializable {
    private static final long serialVersionUID = 5087900373125640764L;

    public ContentAttachment() {
        super();
    }

    public ContentAttachment(ContentAttachmentEntity attachmentEntity) {
        this.setId(attachmentEntity.getId());
        this.setTransaction(attachmentEntity.getTransaction());
        this.setName(attachmentEntity.getName());
        this.setUploadDate(attachmentEntity.getUploadDate());
        this.setContent(attachmentEntity.getContent());
    }

    @Override
    public ContentAttachmentEntity toEntity() {
        ContentAttachmentEntity attachmentEntity = new ContentAttachmentEntity();
        attachmentEntity.setId(this.getId());
        attachmentEntity.setTransaction(this.getTransaction());
        attachmentEntity.setName(this.getName());
        attachmentEntity.setUploadDate(this.getUploadDate());
        attachmentEntity.setContent(this.getContent());
        return attachmentEntity;
    }
}
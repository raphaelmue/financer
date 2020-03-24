package org.financer.server.domain.model.transaction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "transactions_attachments")
public class ContentAttachmentEntity extends AbstractAttachment {
    @Column(name = "content")
    @Lob
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

package org.financer.server.domain.model.transaction;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "transactions_attachments")
public class AttachmentEntity extends AbstractAttachment {
    private static final long serialVersionUID = 7758316425770345150L;
}

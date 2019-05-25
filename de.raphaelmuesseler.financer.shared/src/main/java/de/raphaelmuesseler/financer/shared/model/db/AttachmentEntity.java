package de.raphaelmuesseler.financer.shared.model.db;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "transactions_attachments")
public class AttachmentEntity extends AbstractAttachment {
    private final static long serialVersionUID = 7758316425770345150L;
}

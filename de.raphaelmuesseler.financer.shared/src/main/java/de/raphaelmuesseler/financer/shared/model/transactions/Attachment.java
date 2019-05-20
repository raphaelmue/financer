package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.db.TransactionAttachmentDAO;

import javax.sql.rowset.serial.SerialBlob;
import java.io.Serializable;
import java.sql.SQLException;

public class Attachment extends TransactionAttachmentDAO implements Serializable {
    private static final long serialVersionUID = 5087900373125640764L;
    private byte[] content;

    public Attachment() {
        super();
    }

    public Attachment(TransactionAttachmentDAO databaseAttachment) {
        this.setId(databaseAttachment.getId());
        this.setTransaction(databaseAttachment.getTransaction());
        this.setName(databaseAttachment.getName());
        this.setUploadDate(databaseAttachment.getUploadDate());
        try {
            this.content = databaseAttachment.getContent().getBytes(1, (int) databaseAttachment.getContent().length());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setContent(byte[] content) throws SQLException {
        super.setContent(new SerialBlob(content));
    }

    public byte[] getByteContent() {
        return this.content;
    }

    @Override
    public TransactionAttachmentDAO toDatabaseAccessObject() {
        TransactionAttachmentDAO databaseAttachment = new TransactionAttachmentDAO();
        databaseAttachment.setId(this.getId());
        databaseAttachment.setTransaction(this.getTransaction());
        databaseAttachment.setName(this.getName());
        databaseAttachment.setContent(this.getContent());
        databaseAttachment.setUploadDate(this.getUploadDate());
        return databaseAttachment;
    }
}

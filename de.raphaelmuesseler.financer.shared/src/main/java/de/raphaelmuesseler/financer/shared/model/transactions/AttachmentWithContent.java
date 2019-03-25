package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.db.Attachment;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseObject;

public class AttachmentWithContent extends Attachment {
    private byte[] content;

    public AttachmentWithContent() {
        super();
    }

    @Override
    public DatabaseObject fromDatabaseObject(DatabaseObject databaseObject) {
        if (databaseObject instanceof Attachment) {
            this.setId(databaseObject.getId());
            this.setName(((Attachment) databaseObject).getName());
            this.setContent(((Attachment) databaseObject).getContent());
        }
        return this;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }
}

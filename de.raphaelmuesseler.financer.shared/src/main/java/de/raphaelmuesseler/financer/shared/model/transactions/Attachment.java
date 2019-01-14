package de.raphaelmuesseler.financer.shared.model.transactions;

import de.raphaelmuesseler.financer.shared.model.db.DatabaseAttachment;
import de.raphaelmuesseler.financer.shared.model.db.DatabaseObject;

public class Attachment extends DatabaseAttachment {

    public Attachment() {
        super();
    }

    @Override
    public DatabaseObject fromDatabaseObject(DatabaseObject databaseObject) {
        if (databaseObject instanceof DatabaseAttachment) {
            this.setId(databaseObject.getId());
            this.setName(((DatabaseAttachment) databaseObject).getName());
            this.setContent(((DatabaseAttachment) databaseObject).getContent());
        }
        return this;
    }
}

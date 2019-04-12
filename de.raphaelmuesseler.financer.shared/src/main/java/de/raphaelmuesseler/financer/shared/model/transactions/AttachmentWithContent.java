package de.raphaelmuesseler.financer.shared.model.transactions;

public class AttachmentWithContent extends Attachment {
    private byte[] content;

    public AttachmentWithContent() {
        super();
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

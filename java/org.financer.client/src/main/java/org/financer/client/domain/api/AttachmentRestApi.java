package org.financer.client.domain.api;

import org.financer.client.connection.RestCallback;
import org.financer.client.connection.ServerRequestHandler;
import org.financer.client.domain.model.transaction.Attachment;

public interface AttachmentRestApi {

    /**
     * Inserts a new attachment to the given transaction
     *
     * @param transactionId id of the transaction wo which the attachment will be inserted.
     */
    ServerRequestHandler createAttachment(Long transactionId, Attachment attachment, RestCallback<Attachment> callback);

    /**
     * Fetches an attachment with content
     *
     * @param transactionId id of the transaction
     * @param attachmentId  id of the attachment whose content will be returned
     */
    ServerRequestHandler getAttachment(Long transactionId, Long attachmentId, RestCallback<Attachment> callback);

    /**
     * Deletes a specified attachment.
     *
     * @param transactionId transaction id that refers to the attachment that will be deleted
     * @param attachmentId  id of attachment that will be deleted
     */
    ServerRequestHandler deleteAttachment(Long transactionId, Long attachmentId, RestCallback<Void> callback);

}

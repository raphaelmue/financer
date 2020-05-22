package org.financer.client.domain.api

import org.financer.client.domain.model.transaction.Attachment

interface AttachmentRestApi {
    /**
     * Inserts a new attachment to the given transaction
     *
     * @param transactionId id of the transaction wo which the attachment will be inserted.
     */
    suspend fun createAttachment(transactionId: Long, attachment: Attachment): Attachment?

    /**
     * Fetches an attachment with content
     *
     * @param transactionId id of the transaction
     * @param attachmentId  id of the attachment whose content will be returned
     */
    suspend fun getAttachment(transactionId: Long, attachmentId: Long): Attachment?

    /**
     * Deletes a specified attachment.
     *
     * @param transactionId transaction id that refers to the attachment that will be deleted
     * @param attachmentId  id of attachment that will be deleted
     */
    suspend fun deleteAttachment(transactionId: Long, attachmentId: Long)
}
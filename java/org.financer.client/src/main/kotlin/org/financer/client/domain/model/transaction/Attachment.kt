package org.financer.client.domain.model.transaction

import java.time.LocalDate

class Attachment {
    /*
     * Getters and Setters
     */ var id: Long = 0
        private set
    var transaction: Transaction? = null
        private set
    var name: String? = null
        private set
    var uploadDate: LocalDate? = null
        private set
    lateinit var content: ByteArray
        private set

    fun setId(id: Long): Attachment {
        this.id = id
        return this
    }

    fun setTransaction(transaction: Transaction?): Attachment {
        this.transaction = transaction
        return this
    }

    fun setName(name: String?): Attachment {
        this.name = name
        return this
    }

    fun setUploadDate(uploadDate: LocalDate?): Attachment {
        this.uploadDate = uploadDate
        return this
    }

    fun setContent(content: ByteArray): Attachment {
        this.content = content
        return this
    }
}
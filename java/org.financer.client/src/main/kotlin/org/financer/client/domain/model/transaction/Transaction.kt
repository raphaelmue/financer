package org.financer.client.domain.model.transaction

import org.financer.client.domain.model.category.Category
import org.financer.shared.domain.model.AmountProvider
import java.io.Serializable
import java.util.*

abstract class Transaction : AmountProvider, Serializable {
    /*
     * Getters and Setters
     */ var id: Long = 0
        private set
    var category: Category? = null
        private set
    var description: String? = null
        private set
    var vendor: String? = null
        private set
    private var attachments: MutableSet<Attachment> = HashSet()
    override fun isFixed(): Boolean {
        return this.category!!.isFixed
    }

    override fun isRevenue(): Boolean {
        return this.category!!.isRevenue
    }

    open fun setId(id: Long): Transaction {
        this.id = id
        return this
    }

    open fun setCategory(category: Category?): Transaction {
        this.category = category
        return this
    }

    open fun setDescription(purpose: String?): Transaction {
        description = purpose
        return this
    }

    open fun setVendor(vendor: String?): Transaction {
        this.vendor = vendor
        return this
    }

    fun getAttachments(): Set<Attachment> {
        return attachments
    }

    open fun setAttachments(attachments: MutableSet<Attachment>): Transaction {
        this.attachments = attachments
        return this
    }

    fun addAttachment(attachment: Attachment): Transaction {
        attachments.add(attachment)
        return this
    }

    fun removeAttachment(attachment: Attachment): Transaction {
        attachments.remove(attachment)
        return this
    }

    override fun toString(): String {
        return "Transaction[id=$id, category=$category, vendor='$vendor']"
    }
}
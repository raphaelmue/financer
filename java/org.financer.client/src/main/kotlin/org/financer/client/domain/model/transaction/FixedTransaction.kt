package org.financer.client.domain.model.transaction

import org.financer.client.domain.model.category.Category
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.shared.domain.model.value.objects.ValueDate
import java.time.LocalDate
import java.util.*

class FixedTransaction : Transaction() {
    private var amount: Amount? = null
    var timeRange: TimeRange? = null
        private set
    private var isVariable = false
    var day = 0
        private set
    var product: String? = null
        private set
    private var transactionAmounts: MutableSet<FixedTransactionAmount>? = HashSet()

    /**
     * Indicates whether this transaction is active, i.e. whether the time range of this transaction includes the
     * current date.
     *
     * @return true if the transaction is active, false otherwise
     */
    val isActive: Boolean
        get() = timeRange!!.includes()
    /**
     * Cancels the fixed transaction by setting the end date to the given date.
     *
     * @param endDate end date to be set
     */
    /**
     * Cancels the fixed transaction by setting the end date to current date.
     */
    @JvmOverloads
    fun cancel(endDate: LocalDate? = LocalDate.now()) {
        setTimeRange(timeRange!!.setEndDate(endDate))
    }

    override fun getAmount(valueDate: ValueDate): Amount {
        var result = Amount()
        if (timeRange!!.includes(valueDate)) {
            if (isVariable) {
                if (transactionAmounts != null) {
                    for (transactionAmount in transactionAmounts!!) {
                        result = result.add(transactionAmount.getAmount(valueDate))
                    }
                }
            } else {
                result = this.getAmount()
            }
        }
        return result
    }

    override fun getAmount(timeRange: TimeRange): Amount {
        var result = Amount()
        if (isVariable) {
            for (transactionAmount in transactionAmounts!!) {
                result = result.add(transactionAmount.getAmount(timeRange))
            }
        } else {
            result = this.getAmount()
            result = result.multiply(Amount(this.timeRange!!.getMonthIntersection(timeRange).monthDifference))
        }
        return result
    }

    override fun adjustAmountSign() {
        if (isVariable) {
            for (amountProvider in getTransactionAmounts()) {
                amountProvider.adjustAmountSign()
            }
        } else {
            if (this.isRevenue == this.getAmount().isNegative) {
                setAmount(this.getAmount().adjustSign())
            }
        }
    }

    /*
     * Getters and Setters
     */
    override fun getAmount(): Amount {
        return amount!!
    }

    fun setAmount(amount: Amount?): FixedTransaction {
        this.amount = amount
        return this
    }

    override fun setId(id: Long): FixedTransaction {
        super.setId(id)
        return this
    }

    override fun setCategory(category: Category?): FixedTransaction {
        super.setCategory(category)
        return this
    }

    override fun setDescription(purpose: String?): FixedTransaction {
        super.setDescription(purpose)
        return this
    }

    override fun setVendor(vendor: String?): FixedTransaction {
        super.setVendor(vendor)
        return this
    }

    override fun setAttachments(attachments: MutableSet<Attachment>): FixedTransaction {
        super.setAttachments(attachments)
        return this
    }

    fun setTimeRange(timeRange: TimeRange?): FixedTransaction {
        this.timeRange = timeRange
        return this
    }

    override fun getIsVariable(): Boolean {
        return isVariable
    }

    fun setIsVariable(isVariable: Boolean): FixedTransaction {
        this.isVariable = isVariable
        return this
    }

    fun setDay(day: Int): FixedTransaction {
        this.day = day
        return this
    }

    fun setProduct(product: String?): FixedTransaction {
        this.product = product
        return this
    }

    fun getTransactionAmounts(): Set<FixedTransactionAmount> {
        return (if (isVariable) HashSet() else transactionAmounts)!!
    }

    fun setTransactionAmounts(transactionAmounts: MutableSet<FixedTransactionAmount>?): FixedTransaction {
        this.transactionAmounts = transactionAmounts
        return this
    }

    fun addFixedTransactionAmount(fixedTransactionAmount: FixedTransactionAmount): FixedTransaction {
        transactionAmounts!!.add(fixedTransactionAmount)
        return this
    }

    fun removeFixedTransactionAmount(fixedTransactionAmount: FixedTransactionAmount): FixedTransaction {
        transactionAmounts!!.remove(fixedTransactionAmount)
        return this
    }

    companion object {
        private const val serialVersionUID = 8295185142317654835L
    }
}
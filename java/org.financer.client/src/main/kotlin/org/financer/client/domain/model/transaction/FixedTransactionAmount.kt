package org.financer.client.domain.model.transaction

import org.financer.shared.domain.model.AmountProvider
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.shared.domain.model.value.objects.ValueDate
import java.util.*

class FixedTransactionAmount : AmountProvider {
    /*
     * Getters and Setters
     */ var id: Long = 0
        private set
    var fixedTransaction: FixedTransaction? = null
        private set
    var valueDate: ValueDate? = null
        private set
    private var amount: Amount? = null
    override fun getAmount(valueDate: ValueDate): Amount {
        return if (this.valueDate!!.isInSameMonth(valueDate)) {
            this.getAmount()
        } else {
            Amount()
        }
    }

    override fun getAmount(timeRange: TimeRange): Amount {
        return if (timeRange.includes(valueDate)) {
            this.getAmount()
        } else {
            Amount()
        }
    }

    override fun isFixed(): Boolean {
        return fixedTransaction!!.isFixed
    }

    override fun isRevenue(): Boolean {
        return fixedTransaction!!.isRevenue
    }

    override fun adjustAmountSign() {
        if (this.isRevenue == this.getAmount().isNegative) {
            setAmount(this.getAmount().adjustSign())
        }
    }

    fun setId(id: Long): FixedTransactionAmount {
        this.id = id
        return this
    }

    fun setFixedTransaction(fixedTransaction: FixedTransaction?): FixedTransactionAmount {
        this.fixedTransaction = fixedTransaction
        return this
    }

    fun setValueDate(valueDate: ValueDate?): FixedTransactionAmount {
        this.valueDate = valueDate
        return this
    }

    override fun getAmount(): Amount {
        return amount!!
    }

    fun setAmount(amount: Amount?): FixedTransactionAmount {
        this.amount = amount
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as FixedTransactionAmount
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "FixedTransactionAmount [" +
                "id=" + id +
                ", valueDate=" + valueDate +
                ", amount=" + amount +
                ']'
    }
}
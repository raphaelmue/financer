package org.financer.client.domain.model.transaction

import org.financer.shared.domain.model.AmountProvider
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.Quantity
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.shared.domain.model.value.objects.ValueDate
import java.util.*

class Product : AmountProvider {
    /*
     * Getters and Setters
     */ var id: Long = 0
        private set
    var transaction: VariableTransaction? = null
        private set
    var name: String? = null
        private set
    var quantity: Quantity? = null
        private set
    private var amount: Amount? = null
    override fun getAmount(): Amount {
        return amount!!.calculate(quantity)
    }

    override fun getAmount(valueDate: ValueDate): Amount {
        return if (transaction!!.valueDate!!.isInSameMonth(valueDate)) {
            amount!!.calculate(quantity)
        } else Amount()
    }

    override fun getAmount(timeRange: TimeRange): Amount {
        return if (timeRange.includes(transaction!!.valueDate)) {
            amount!!.calculate(quantity)
        } else Amount()
    }

    override fun isFixed(): Boolean {
        return transaction!!.isFixed
    }

    override fun isRevenue(): Boolean {
        return transaction!!.isRevenue
    }

    override fun adjustAmountSign() {
        if (this.isRevenue == this.getAmount().isNegative) {
            setAmount(this.getAmount().adjustSign())
        }
    }

    fun setId(id: Long): Product {
        this.id = id
        return this
    }

    fun setTransaction(transaction: VariableTransaction?): Product {
        this.transaction = transaction
        return this
    }

    fun setName(name: String?): Product {
        this.name = name
        return this
    }

    fun setQuantity(quantity: Quantity?): Product {
        this.quantity = quantity
        return this
    }

    fun setAmount(amount: Amount?): Product {
        this.amount = amount
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val product = o as Product
        return id == product.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "Product [" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ']'
    }
}
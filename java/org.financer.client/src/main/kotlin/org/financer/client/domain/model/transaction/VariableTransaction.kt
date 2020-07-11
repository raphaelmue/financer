package org.financer.client.domain.model.transaction

import org.financer.client.domain.model.category.Category
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.shared.domain.model.value.objects.ValueDate
import java.util.*

class VariableTransaction : Transaction() {
    /*
     * Getters and Setters
     */ var valueDate: ValueDate? = null
        private set
    private var products: MutableSet<Product> = HashSet()
    override fun getAmount(): Amount {
        var amount = Amount()
        for (amountProvider in products) {
            amount = amount.add(amountProvider.amount)
        }
        return amount
    }

    override fun getAmount(valueDate: ValueDate): Amount {
        var amount = Amount()
        for (amountProvider in products) {
            amount = amount.add(amountProvider.getAmount(valueDate))
        }
        return amount
    }

    override fun getAmount(timeRange: TimeRange): Amount {
        var amount = Amount()
        for (amountProvider in products) {
            amount = amount.add(amountProvider.getAmount(timeRange))
        }
        return amount
    }

    override fun adjustAmountSign() {
        for (amountProvider in getProducts()) {
            amountProvider.adjustAmountSign()
        }
    }

    fun setValueDate(valueDate: ValueDate?): VariableTransaction {
        this.valueDate = valueDate
        return this
    }

    fun getProducts(): Set<Product> {
        return products
    }

    fun setProducts(products: MutableSet<Product>): VariableTransaction {
        this.products = products
        return this
    }

    fun addProduct(product: Product): VariableTransaction {
        products.add(product)
        return this
    }

    override fun setId(id: Long): VariableTransaction {
        super.setId(id)
        return this
    }

    override fun setCategory(category: Category?): VariableTransaction {
        super.setCategory(category)
        return this
    }

    override fun setDescription(purpose: String?): VariableTransaction {
        super.setDescription(purpose)
        return this
    }

    override fun setVendor(vendor: String?): VariableTransaction {
        super.setVendor(vendor)
        return this
    }

    override fun setAttachments(attachments: MutableSet<Attachment>): VariableTransaction {
        super.setAttachments(attachments)
        return this
    }
}
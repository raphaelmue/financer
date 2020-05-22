package org.financer.client.domain.model.category

import org.financer.shared.domain.model.AmountProvider
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.CategoryClass
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.shared.domain.model.value.objects.ValueDate
import org.financer.util.collections.Tree
import java.io.Serializable
import java.util.stream.Collectors

class CategoryRoot(val categories: Set<Category>) : Serializable, AmountProvider, Tree {
    fun getCategoriesByClass(categoryClass: CategoryClass): Set<Category> {
        return this.getCategoriesByClass(categoryClass.categoryClass)
    }

    fun getCategoriesByClass(categoryClass: CategoryClass.Values): Set<Category> {
        return categories.stream()
                .filter { category: Category -> category.categoryClass!!.categoryClass == categoryClass }
                .collect(Collectors.toSet())
    }

    override fun getAmount(): Amount {
        var amount = Amount()
        for (category in categories) {
            amount = amount.add(category.amount)
        }
        return amount
    }

    fun getAmount(categoryClass: CategoryClass.Values): Amount {
        var amount = Amount()
        for (category in getCategoriesByClass(categoryClass)) {
            amount = amount.add(category.amount)
        }
        return amount
    }

    override fun getAmount(valueDate: ValueDate): Amount {
        var amount = Amount()
        for (category in categories) {
            amount = amount.add(category.getAmount(valueDate))
        }
        return amount
    }

    fun getAmount(categoryClass: CategoryClass.Values, valueDate: ValueDate): Amount {
        var amount = Amount()
        for (category in getCategoriesByClass(categoryClass)) {
            amount = amount.add(category.getAmount(valueDate))
        }
        return amount
    }

    override fun getAmount(timeRange: TimeRange): Amount {
        var amount = Amount()
        for (category in categories) {
            amount = amount.add(category.getAmount(timeRange))
        }
        return amount
    }

    override fun isFixed(): Boolean {
        throw UnsupportedOperationException("The category root cannot have any category class")
    }

    override fun isRevenue(): Boolean {
        throw UnsupportedOperationException("The category root cannot have any category class")
    }

    override fun adjustAmountSign() {
        for (category in categories) {
            category.adjustAmountSign()
        }
    }

    override fun getParent(): Tree {
        throw UnsupportedOperationException("The category root cannot have a parent element.")
    }

    override fun setParent(parent: Tree): Tree {
        throw IllegalStateException("Root category cannot have a parent element.")
    }

    override fun isRoot(): Boolean {
        return true
    }

    override fun getChildren(): Set<Tree> {
        return categories
    }

    companion object {
        private const val serialVersionUID = -2680853209855531689L
    }

}
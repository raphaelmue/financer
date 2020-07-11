package org.financer.client.domain.model.category

import org.financer.client.domain.model.transaction.Transaction
import org.financer.client.domain.model.user.User
import org.financer.shared.domain.model.AmountProvider
import org.financer.shared.domain.model.Formattable
import org.financer.shared.domain.model.Settings
import org.financer.shared.domain.model.value.objects.Amount
import org.financer.shared.domain.model.value.objects.CategoryClass
import org.financer.shared.domain.model.value.objects.TimeRange
import org.financer.shared.domain.model.value.objects.ValueDate
import org.financer.util.collections.Tree
import java.io.Serializable
import java.util.*

class Category : Serializable, Tree, AmountProvider, Formattable {
    /*
     * Getters and Setters
     */ var id: Long = 0
        private set
    var user: User? = null
        private set
    var categoryClass: CategoryClass? = null
        private set
    private var parent: Category? = null
    var name: String? = null
        private set
    private var children: Set<Category>? = null
    val transactions: MutableSet<Transaction> = HashSet()
    val prefix: String
        get() {
            if (this.isRoot) {
                return (categoryClass!!.categoryClass.ordinal + 1).toString()
            }
            val neighbors: List<Category> = ArrayList(parent!!.getChildren())
            neighbors.sortedWith(compareBy { it.name })
            val index = neighbors.indexOf(this) + 1
            return parent!!.prefix + "." + index
        }

    override fun getAmount(): Amount {
        var amount = Amount()
        for (amountProvider in transactions) {
            amount = amount.add(amountProvider.amount)
        }
        if (!this.isLeaf) {
            for (amountProvider in children!!) {
                amount = amount.add(amountProvider.amount)
            }
        }
        return amount
    }

    override fun getAmount(valueDate: ValueDate): Amount {
        var amount = Amount()
        for (amountProvider in transactions) {
            amount = amount.add(amountProvider.getAmount(valueDate))
        }
        if (this.isLeaf) {
            for (amountProvider in children!!) {
                amount = amount.add(amountProvider.getAmount(valueDate))
            }
        }
        return amount
    }

    override fun getAmount(timeRange: TimeRange): Amount {
        var amount = Amount()
        for (amountProvider in transactions) {
            amount = amount.add(amountProvider.getAmount(timeRange))
        }
        if (!this.isLeaf) {
            for (amountProvider in children!!) {
                amount = amount.add(amountProvider.getAmount(timeRange))
            }
        }
        return amount
    }

    override fun isFixed(): Boolean {
        return categoryClass!!.isFixed
    }

    override fun isRevenue(): Boolean {
        return categoryClass!!.isRevenue
    }

    override fun adjustAmountSign() {
        for (amountProvider in transactions) {
            amountProvider.adjustAmountSign()
        }
        if (!this.isLeaf) {
            for (amountProvider in children!!) {
                amountProvider.adjustAmountSign()
            }
        }
    }

    override fun format(settings: Settings): String {
        val prefix = prefix
        return if (!prefix.isBlank()) {
            this.prefix + " " + name
        } else name!!
    }

    fun setId(id: Long): Category {
        this.id = id
        return this
    }

    fun setUser(user: User?): Category {
        this.user = user
        return this
    }

    fun setCategoryClass(categoryClass: CategoryClass?): Category {
        this.categoryClass = categoryClass
        return this
    }

    override fun getParent(): Category {
        return parent!!
    }

    override fun setParent(parent: Tree): Category {
        this.parent = parent as Category
        return this
    }

    fun setName(name: String?): Category {
        this.name = name
        return this
    }

    override fun getChildren(): Set<Category> {
        return children!!
    }

    fun setChildren(children: Set<Category>?): Category {
        this.children = children
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val category = o as Category
        return id == category.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "Category[id=$id, categoryClass=$categoryClass, name='$name']"
    }

    companion object {
        private const val serialVersionUID = 5491420625985358596L
    }
}
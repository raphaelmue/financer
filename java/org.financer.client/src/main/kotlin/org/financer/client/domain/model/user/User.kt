package org.financer.client.domain.model.user

import org.financer.client.domain.model.category.Category
import org.financer.shared.domain.model.Settings
import org.financer.shared.domain.model.value.objects.*
import java.io.Serializable
import java.util.*

class User : Serializable, Settings {
    var id: Long? = null
    var email: Email? = null
        private set
    var password: HashedPassword? = null
        private set
    var name: Name? = null
        private set
    var birthDate: BirthDate? = null
        private set
    var gender: Gender? = null
        private set
    var categories: Set<Category> = HashSet()
        private set
    var settings: MutableMap<SettingPair.Property?, Setting?>? = null
        private set
    var tokens: Set<Token> = HashSet()
        private set
    var activeToken: Token? = null
        private set
    var isVerified = false
        private set

    override fun <T> putOrUpdateSettingProperty(property: SettingPair.Property, value: T) {
        if (settings!!.containsKey(property)) {
            settings!![property]!!.setValue(value.toString())
        } else {
            settings!![property] = Setting()
                    .setUser(this)
                    .setPair(SettingPair(property, value.toString()))
        }
    }

    override fun <T> getValue(property: SettingPair.Property): T {
        return settings!![property]!!.pair!!.valueObject as T
    }

    /*
     * Getters and Setters
     */
    fun getId(): Long {
        return id!!
    }

    fun setId(id: Long): User {
        this.id = id
        return this
    }

    fun setEmail(email: Email?): User {
        this.email = email
        return this
    }

    fun setPassword(password: HashedPassword?): User {
        this.password = password
        return this
    }

    fun setName(name: Name?): User {
        this.name = name
        return this
    }

    fun setBirthDate(birthDate: BirthDate?): User {
        this.birthDate = birthDate
        return this
    }

    fun setGender(gender: Gender?): User {
        this.gender = gender
        return this
    }

    fun setCategories(categories: Set<Category>): User {
        this.categories = categories
        return this
    }

    fun setSettings(settings: MutableMap<SettingPair.Property?, Setting?>?): User {
        this.settings = settings
        return this
    }

    fun setTokens(tokens: Set<Token>): User {
        this.tokens = tokens
        return this
    }

    fun setActiveToken(activeToken: Token?): User {
        this.activeToken = activeToken
        return this
    }

    fun setVerified(verified: Boolean): User {
        isVerified = verified
        return this
    }

    companion object {
        private const val serialVersionUID = 8551108621522985674L
    }
}
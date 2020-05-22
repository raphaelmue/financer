package org.financer.client.domain.model.user

import org.financer.shared.domain.model.value.objects.SettingPair

class Setting {
    /*
     * Getters and Setters
     */ var id: Long = 0
        private set
    var user: User? = null
        private set
    var pair: SettingPair? = null
        private set

    fun setValue(value: String?): Setting {
        setPair(pair!!.setValue(value))
        return this
    }

    fun setId(id: Long): Setting {
        this.id = id
        return this
    }

    fun setUser(user: User?): Setting {
        this.user = user
        return this
    }

    fun setPair(pair: SettingPair?): Setting {
        this.pair = pair
        return this
    }
}
package org.financer.client.domain.model.user

import org.financer.shared.domain.model.value.objects.ExpireDate
import org.financer.shared.domain.model.value.objects.IPAddress
import org.financer.shared.domain.model.value.objects.OperatingSystem
import org.financer.shared.domain.model.value.objects.TokenString
import java.util.*

class Token {
    /*
     * Getter and Setter
     */ var id: Long = 0
        private set
    var user: User? = null
        private set
    var token: TokenString? = null
        private set
    var expireDate: ExpireDate? = null
        private set
    var ipAddress: IPAddress? = null
        private set
    var operatingSystem: OperatingSystem? = null
        private set

    fun setId(id: Long): Token {
        this.id = id
        return this
    }

    fun setUser(user: User?): Token {
        this.user = user
        return this
    }

    fun setToken(token: TokenString?): Token {
        this.token = token
        return this
    }

    fun setExpireDate(expireDate: ExpireDate?): Token {
        this.expireDate = expireDate
        return this
    }

    fun setIpAddress(ipAddress: IPAddress?): Token {
        this.ipAddress = ipAddress
        return this
    }

    fun setOperatingSystem(operatingSystem: OperatingSystem?): Token {
        this.operatingSystem = operatingSystem
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Token
        return if (id >= 0) {
            id == that.id
        } else {
            token == that.token
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    companion object {
        private const val serialVersionUID = 8834445127500149942L
    }
}
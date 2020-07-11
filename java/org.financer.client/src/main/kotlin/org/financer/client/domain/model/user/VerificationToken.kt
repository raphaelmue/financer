package org.financer.client.domain.model.user

import org.financer.shared.domain.model.value.objects.ExpireDate
import org.financer.shared.domain.model.value.objects.TokenString
import java.time.LocalDate
import java.util.*

class VerificationToken {
    var id: Long = 0
        private set
    var user: User? = null
        private set
    var token: TokenString? = null
        private set
    var expireDate: ExpireDate? = null
        private set
    var verifyingDate: LocalDate? = null

    fun setId(id: Long): VerificationToken {
        this.id = id
        return this
    }

    fun setUser(user: User?): VerificationToken {
        this.user = user
        return this
    }

    fun setToken(token: TokenString?): VerificationToken {
        this.token = token
        return this
    }

    fun setExpireDate(expireDate: ExpireDate?): VerificationToken {
        this.expireDate = expireDate
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as VerificationToken
        return id == that.id &&
                token == that.token
    }

    override fun hashCode(): Int {
        return Objects.hash(id, token)
    }
}
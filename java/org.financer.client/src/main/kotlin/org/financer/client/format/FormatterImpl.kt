package org.financer.client.format

import org.financer.client.domain.model.user.User
import org.financer.client.local.LocalStorage
import org.financer.shared.domain.model.Formattable
import org.financer.shared.domain.model.value.objects.SettingPair
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

open class FormatterImpl : Formatter {
    @kotlin.jvm.JvmField
    protected val user: User?

    constructor(localStorage: LocalStorage) {
        user = localStorage.readObject("user")
    }

    constructor(user: User?) {
        this.user = user
    }

    override fun format(formattable: Formattable): String? {
        return formattable.format(user)
    }

    override fun format(localDate: LocalDate): String? {
        val locale = user!!.getValueOrDefault<Locale>(SettingPair.Property.LANGUAGE)
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
        return localDate.format(formatter)
    }

    fun format(value: Double?): String {
        val locale = user!!.getValueOrDefault<Locale>(SettingPair.Property.LANGUAGE)
        return String.format(locale, "%.2f", value) + " "
    }
}
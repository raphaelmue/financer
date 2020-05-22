package org.financer.client.format

import org.financer.client.domain.model.user.User
import org.financer.client.local.LocalStorage
import org.financer.shared.domain.model.value.objects.SettingPair
import java.io.Serializable
import java.text.MessageFormat
import java.util.*

/**
 * I18N utility class..
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
object I18N {
    /**
     * the current selected Locale.
     */
    private var localStorage: LocalStorage? = null

    @kotlin.jvm.JvmStatic
    fun setLocalStorage(localStorage: LocalStorage?) {
        I18N.localStorage = localStorage
    }

    /**
     * getObject the supported Locales.
     *
     * @return List of Locale objects.
     */
    private val supportedLocales: List<Locale>
        get() = ArrayList(listOf(Locale.ENGLISH, Locale.GERMAN))

    /**
     * getObject the default locale. This is the systems default if contained in the supported locales, english
     * otherwise.
     *
     * @return default locale (en)
     */
    private val defaultLocale: Locale
        get() {
            val sysDefault = Locale.getDefault()
            return if (supportedLocales.contains(sysDefault)) sysDefault else Locale.ENGLISH
        }

    private val locale: Locale
        get() = if (localStorage!!.readObject<Serializable?>("user") != null) {
            (localStorage!!.readObject<Serializable>("user") as User).getValueOrDefault(SettingPair.Property.LANGUAGE)
        } else {
            SettingPair.Property.LANGUAGE.defaultValue as Locale
        }

    /**
     * gets the string with the given key from the resource bundle for the current locale and uses it as first argument
     * to MessageFormat.format, passing in the optional args and returning the result.
     *
     * @param key  message key
     * @param args optional arguments for the message
     * @return localized formatted string
     */
    @kotlin.jvm.JvmStatic
    operator fun get(key: String?, vararg args: Any?): String {
        val bundle = ResourceBundle.getBundle("Financer", locale)
        return MessageFormat.format(bundle.getString(key), *args)
    }

    enum class Language(val language: String, val locale: Locale) {
        ENGLISH("English", Locale.ENGLISH), GERMAN("German", Locale.GERMAN);

        override fun toString(): String {
            return language
        }

        companion object {
            @kotlin.jvm.JvmStatic
            val all: List<Language>
                get() = listOf(*values())

            @kotlin.jvm.JvmStatic
            fun getLanguageByLocale(locale: Locale): Language? {
                for (language in values()) {
                    if (language.locale == locale) {
                        return language
                    }
                }
                return null
            }
        }

    }
}
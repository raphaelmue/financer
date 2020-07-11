package org.financer.client.javafx.components

import com.jfoenix.controls.JFXDatePicker
import javafx.util.StringConverter
import org.financer.client.format.Formatter
import org.financer.client.javafx.format.JavaFXFormatter
import org.financer.client.javafx.local.LocalStorageImpl
import org.financer.shared.domain.model.Settings
import org.financer.shared.domain.model.value.objects.SettingPair
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class DatePicker @JvmOverloads constructor(formatter: Formatter = JavaFXFormatter(LocalStorageImpl.getInstance())) : JFXDatePicker() {
    init {
        converter = object : StringConverter<LocalDate?>() {
            override fun toString(localDate: LocalDate?): String {
                return if (localDate == null) {
                    ""
                } else {
                    formatter.format(localDate)!!
                }
            }

            override fun fromString(dateString: String): LocalDate? {
                return if (dateString.trim { it <= ' ' }.isEmpty()) {
                    null
                } else {
                    val locale = (LocalStorageImpl.getInstance().readObject<Serializable>("user") as Settings?)!!.getValueOrDefault<Locale>(SettingPair.Property.LANGUAGE)
                    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
                    LocalDate.parse(dateString, formatter)
                }
            }
        }
    }
}
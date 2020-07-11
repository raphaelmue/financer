package org.financer.client.format

import org.financer.shared.domain.model.Formattable
import java.time.LocalDate

/**
 * Formatter class that is able to format [Formattable] objects. Wrapper class to provide the users settings for
 * the given formattable.
 *
 * @author Raphael Müßeler
 */
interface Formatter {
    /**
     * Formats the given formattable and returns the formatted string
     *
     * @param formattable object to be formatted
     * @return formatted string
     */
    fun format(formattable: Formattable): String?
    fun format(localDate: LocalDate): String?
}
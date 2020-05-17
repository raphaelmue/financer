package org.financer.client.format;

import org.financer.shared.domain.model.Formattable;

import java.time.LocalDate;

/**
 * Formatter class that is able to format {@link Formattable} objects. Wrapper class to provide the users settings for
 * the given formattable.
 *
 * @author Raphael Müßeler
 */
public interface Formatter {

    /**
     * Formats the given formattable and returns the formatted string
     *
     * @param formattable object to be formatted
     * @return formatted string
     */
    String format(Formattable formattable);

    String format(LocalDate localDate);

}

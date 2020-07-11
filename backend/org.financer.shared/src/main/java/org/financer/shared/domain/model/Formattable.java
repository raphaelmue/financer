package org.financer.shared.domain.model;

/**
 * Represents classes that can be formatted so that the can be displayed.
 *
 * @author Raphael Müßeler
 */
public interface Formattable {

    /**
     * Formats the current object
     *
     * @return formatted string
     */
    String format(Settings settings);

}

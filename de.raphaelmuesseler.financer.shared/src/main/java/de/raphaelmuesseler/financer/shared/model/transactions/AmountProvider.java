package de.raphaelmuesseler.financer.shared.model.transactions;

import java.time.LocalDate;

/**
 * This class is for calculating amounts.
 */
public interface AmountProvider {

    /**
     * Returns the amount of the current month
     *
     * @return amount of month
     */
    double getAmount();

    /**
     * Returns the amount of the specified month and year.
     *
     * @param localDate specifies month and year
     * @return amount of month and year
     */
    double getAmount(LocalDate localDate);

    /**
     * Returns the amount within a specified time period.
     *
     * @param startDate start date of time period
     * @param endDate   end date of time period
     * @return amount within the time period
     */
    double getAmount(LocalDate startDate, LocalDate endDate);

}

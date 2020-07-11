package org.financer.shared.domain.model;

import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;
import org.financer.shared.domain.model.value.objects.ValueDate;

/**
 * This class is for calculating amounts.
 */
public interface AmountProvider {

    /**
     * Returns the amount
     *
     * @return amount of month
     */
    Amount getAmount();

    /**
     * Returns the amount of the specified month and year.
     *
     * @param valueDate specifies month and year
     * @return amount of month and year
     */
    Amount getAmount(ValueDate valueDate);

    /**
     * Returns the amount within a specified time period.
     *
     * @param timeRange time range
     * @return amount within the time period
     */
    Amount getAmount(TimeRange timeRange);

    /**
     * Indicates whether this refers to either fixed revenue or fixed expenses.
     *
     * @return true if fixed
     */
    boolean isFixed();

    /**
     * Indicates whether this refers to either variable revenue or variable expenses.
     *
     * @return true if variable
     */
    default boolean getIsVariable() {
        return !isFixed();
    }

    /**
     * Indicates whether this is fixed or variable revenue.
     *
     * @return true if revenue
     */
    boolean isRevenue();

    /**
     * Indicates whether this is fixed or variable expenses
     *
     * @return true if expenses
     */
    default boolean isExpenses() {
        return !isRevenue();
    }

    /**
     * Adjusts the amount sign, if necessary. If the category is a revenue category and the amount is negative or vice
     * versa, the amount sign will be changed.
     */
    public void adjustAmountSign();

}

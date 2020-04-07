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

}
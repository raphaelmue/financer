package de.raphaelmuesseler.financer.shared.model;

import java.time.LocalDate;

public interface AmountProvider {

    double getAmount();
    double getAmount(LocalDate localDate);
    double getAmount(LocalDate startDate, LocalDate endDate);

}

package de.raphaelmuesseler.financer.client.format;

import de.raphaelmuesseler.financer.shared.exceptions.FinancerException;
import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;

import java.time.LocalDate;

public interface Formatter {
    String formatExceptionMessage(FinancerException exception);

    String formatCurrency(Double amount);

    String formatCategoryName(Category category);

    String formatCategoryName(CategoryTree categoryTree);

    String formatDate(LocalDate localDate);
}

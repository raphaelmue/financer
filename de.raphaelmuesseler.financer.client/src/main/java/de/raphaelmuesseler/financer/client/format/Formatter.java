package de.raphaelmuesseler.financer.client.format;

import de.raphaelmuesseler.financer.shared.model.categories.Category;
import de.raphaelmuesseler.financer.shared.model.categories.CategoryTree;

import java.time.LocalDate;

public interface Formatter {
    String formatExceptionMessage(Exception exception);

    String formatCurrency(Double amount);

    String formatCategoryName(Category category);

    String formatCategoryName(CategoryTree categoryTree);

    String formatDate(LocalDate localDate);

    String formatMonth(LocalDate localDate);

    LocalDate convertStringToLocalDate(String dateString);
}

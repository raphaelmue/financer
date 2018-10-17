package de.raphaelmuesseler.financer.client.javafx.format;

import de.raphaelmuesseler.financer.client.format.Formatter;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;

public class JavaFXFormatter extends Formatter {

    public static String formatCategoryName(CategoryTree categoryTree) {
        if (categoryTree.getValue().getPrefix() != null) {
            return categoryTree.getValue().getPrefix() + " " +
                    (categoryTree.getValue().getName().equals(categoryTree.getCategoryClass().getName()) ?
                            I18N.get(categoryTree.getValue().getName()) : categoryTree.getValue().getName());
        } else {
            return (categoryTree.getValue().getName().equals(categoryTree.getCategoryClass().getName()) ?
                    I18N.get(categoryTree.getValue().getName()) : categoryTree.getValue().getName());
        }
    }

}

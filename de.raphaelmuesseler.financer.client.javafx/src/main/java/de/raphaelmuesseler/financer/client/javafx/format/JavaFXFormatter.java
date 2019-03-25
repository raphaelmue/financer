package de.raphaelmuesseler.financer.client.javafx.format;

import de.raphaelmuesseler.financer.client.format.FormatterImpl;
import de.raphaelmuesseler.financer.client.format.I18N;
import de.raphaelmuesseler.financer.client.local.LocalSettings;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.shared.model.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.Category;
import de.raphaelmuesseler.financer.shared.model.CategoryTree;
import de.raphaelmuesseler.financer.shared.model.user.User;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class JavaFXFormatter extends FormatterImpl {

    public JavaFXFormatter(LocalStorage localStorage) {
        super(localStorage);
    }

    public JavaFXFormatter(LocalSettings localSettings, User user) {
        super(localSettings, user);
    }

    public Label formatAmountLabel(Double amount) {
        return formatAmountLabel(new Label(), amount);
    }

    public Label formatAmountLabel(Label amountLabel, Double amount) {
        amountLabel.setText(formatCurrency(amount));
        amountLabel.setTextAlignment(TextAlignment.RIGHT);
        amountLabel.setAlignment(Pos.CENTER_RIGHT);
        amountLabel.getStyleClass().add(amount < 0 ? "neg-amount" : "pos-amount");
        return amountLabel;
    }

    @Override
    public String formatCategoryName(Category category) {
        StringBuilder result = new StringBuilder();
        if (category.getPrefix() != null) {
            result.append(category.getPrefix()).append(" ");
        }

        if (BaseCategory.CategoryClass.getCategoryClassByName(category.getName()) != null) {
            result.append(I18N.get(category.getName()));
        } else if (category.getName().equals("root")) {
            result.append(I18N.get("balance"));
        } else {
            result.append(category.getName());
        }
        return result.toString();
    }

    @Override
    public String formatCategoryName(CategoryTree categoryTree) {
        return this.formatCategoryName(categoryTree.getValue());
    }
}

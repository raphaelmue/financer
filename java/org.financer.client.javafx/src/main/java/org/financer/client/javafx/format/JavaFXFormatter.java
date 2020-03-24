package org.financer.client.javafx.format;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import org.financer.client.format.FormatterImpl;
import org.financer.client.format.I18N;
import org.financer.client.local.LocalStorage;

public class JavaFXFormatter extends FormatterImpl {

    public JavaFXFormatter(LocalStorage localStorage) {
        super(localStorage);
    }

    public JavaFXFormatter(User user) {
        super(user);
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

    public Label formatChangeLabel(Double amount) {
        return formatChangeLabel(new Label(), amount);
    }

    public Label formatChangeLabel(Label label, double amount) {
        if (!Double.isNaN(amount) && Double.isFinite(amount)) {
            label.setText(String.format(user.getSettings().getLanguage(), "%.1f", amount) + "%");
        } else {
            label.setText("---");
        }
        label.setTextAlignment(TextAlignment.RIGHT);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.getStyleClass().add(amount < 0 ? "neg-amount" : "pos-amount");
        return label;
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

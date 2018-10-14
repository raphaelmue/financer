package de.raphaelmuesseler.financer.client.format;

import de.raphaelmuesseler.financer.client.local.Settings;
import de.raphaelmuesseler.financer.shared.exceptions.FinancerException;
import de.raphaelmuesseler.financer.shared.model.Category;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

import java.util.Locale;

public class Formatter {
    private static Settings SETTINGS;

    public static void setSettings(Settings SETTINGS) {
        Formatter.SETTINGS = SETTINGS;
    }

    public static String formatExceptionMessage(FinancerException exception) {
        return I18N.get(exception.getDisplayMessage());
    }

    public static String formatCurrency(Double amount) {
        if (SETTINGS != null) {
            return (SETTINGS.getLanguage() == Locale.GERMAN ? Double.toString(amount).replace(".", ",") : Double.toString(amount)) +
                    " " + (SETTINGS.isShowCurrencySign() ? SETTINGS.getCurrency().getSymbol() : SETTINGS.getCurrency());
        } else {
            return Double.toString(amount);
        }
    }

    public static Label formatAmountLabel(Double amount) {
        return formatAmountLabel(new Label(), amount);
    }

    public static Label formatAmountLabel(Label amountLabel, Double amount) {
        amountLabel.setText(formatCurrency(amount));
        amountLabel.setTextAlignment(TextAlignment.RIGHT);
        amountLabel.setAlignment(Pos.CENTER_RIGHT);
        amountLabel.getStyleClass().add(amount < 0 ? "neg-amount" : "pos-amount");
        return amountLabel;
    }

    public static String formatCategoryName(Category category) {
        if (category.getPrefix() != null) {
            return category.getPrefix() + " " + category.getName();
        } else {
            return category.getName();
        }
    }
}

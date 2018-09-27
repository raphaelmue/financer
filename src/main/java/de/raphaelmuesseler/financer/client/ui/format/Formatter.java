package de.raphaelmuesseler.financer.client.ui.format;

import de.raphaelmuesseler.financer.client.local.LocalStorage;
import de.raphaelmuesseler.financer.client.local.Settings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

import java.util.Locale;

public class Formatter {
    private static final Settings SETTINGS = LocalStorage.getSettings();

    public static String formatCurrency(Double amount) {
        return (SETTINGS.getLanguage() == Locale.GERMAN ? Double.toString(amount).replace(".", ",") : Double.toString(amount)) +
                " " + (SETTINGS.isShowCurrencySign() ? SETTINGS.getCurrency().getSymbol() : SETTINGS.getCurrency());
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
}

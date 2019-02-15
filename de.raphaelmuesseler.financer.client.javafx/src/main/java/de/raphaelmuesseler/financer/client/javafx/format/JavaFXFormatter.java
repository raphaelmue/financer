package de.raphaelmuesseler.financer.client.javafx.format;

import de.raphaelmuesseler.financer.client.format.FormatterImpl;
import de.raphaelmuesseler.financer.client.local.LocalStorage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class JavaFXFormatter extends FormatterImpl {

    public JavaFXFormatter(LocalStorage localStorage) {
        super(localStorage);
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
}

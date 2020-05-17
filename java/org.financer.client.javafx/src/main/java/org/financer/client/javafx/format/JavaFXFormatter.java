package org.financer.client.javafx.format;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import org.financer.client.domain.model.user.User;
import org.financer.client.format.FormatterImpl;
import org.financer.client.local.LocalStorage;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.SettingPair;

import java.util.Locale;

public class JavaFXFormatter extends FormatterImpl {

    public JavaFXFormatter(LocalStorage localStorage) {
        super(localStorage);
    }

    public JavaFXFormatter(User user) {
        super(user);
    }

    public Label format(Label amountLabel, Amount amount) {
        amountLabel.setText(format(amount));
        amountLabel.setTextAlignment(TextAlignment.RIGHT);
        amountLabel.setAlignment(Pos.CENTER_RIGHT);
        amountLabel.getStyleClass().add(amount.getAmount() < 0 ? "neg-amount" : "pos-amount");
        return amountLabel;
    }

    public Label formatChangeLabel(Label label, double amount) {
        if (!Double.isNaN(amount) && Double.isFinite(amount)) {
            label.setText(String.format((Locale) user.getValueOrDefault(SettingPair.Property.LANGUAGE), "%.1f", amount) + "%");
        } else {
            label.setText("---");
        }
        label.setTextAlignment(TextAlignment.RIGHT);
        label.setAlignment(Pos.CENTER_RIGHT);
        label.getStyleClass().add(amount < 0 ? "neg-amount" : "pos-amount");
        return label;
    }
}

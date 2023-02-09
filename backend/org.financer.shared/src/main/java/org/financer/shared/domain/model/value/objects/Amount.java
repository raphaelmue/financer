package org.financer.shared.domain.model.value.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.financer.shared.domain.model.Formattable;
import org.financer.shared.domain.model.Settings;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;

@Data
@Embeddable
@Immutable
@Schema(description = "Value object for amount")
public class Amount implements Serializable, Formattable {
    private static final long serialVersionUID = 8647653287643900256L;

    @EqualsAndHashCode.Include
    @Column(name = "amount", nullable = false)
    @Schema(description = "Amount property", required = true, example = "50.0")
    private final double amount;

    public Amount() {
        this.amount = 0;
    }

    public Amount(int amount) {
        this.amount = amount;
    }

    public Amount(double amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public boolean isPositive() {
        return this.amount >= 0;
    }

    @JsonIgnore
    public boolean isNegative() {
        return !this.isPositive();
    }

    public boolean isNotNull() {
        return this.getAmount() != 0;
    }

    public Amount calculate(Quantity quantity) {
        return new Amount(this.getAmount() * quantity.getNumberOfItems());
    }

    public Amount adjustSign() {
        return new Amount(this.getAmount() * (-1));
    }

    public Amount add(Amount value) {
        return new Amount(this.amount + value.getAmount());
    }

    public Amount subtract(Amount value) {
        return new Amount(this.amount - value.getAmount());
    }

    public Amount subtract(double value) {
        return new Amount(this.amount - value);
    }

    public Amount multiply(Amount value) {
        return new Amount(this.amount * value.getAmount());
    }

    public Amount multiply(double value) {
        return new Amount(this.amount * value);
    }

    public Amount divide(Amount divisor) {
        if (divisor.getAmount() != 0) {
            return new Amount(this.amount / divisor.getAmount());
        }
        throw new IllegalArgumentException("Divisor may not be 0.");
    }

    public Amount divide(double divisor) {
        return this.divide(new Amount(divisor));
    }

    public Amount calcRatio(Amount ratio) {
        return this.divide(ratio).multiply(100);
    }

    @Override
    public String format(Settings settings) {
        Locale locale = settings.getValueOrDefault(SettingPair.Property.LANGUAGE);
        Currency currency = settings.getValueOrDefault(SettingPair.Property.CURRENCY);
        StringBuilder result = new StringBuilder(String.format(locale, "%.2f", amount)).append(" ");
        if (settings.getValueOrDefault(SettingPair.Property.SHOW_CURRENCY_SIGN).equals(Boolean.toString(true))) {
            result.append(currency.getSymbol());
        } else {
            result.append(currency.getCurrencyCode());
        }
        return result.toString();
    }
}

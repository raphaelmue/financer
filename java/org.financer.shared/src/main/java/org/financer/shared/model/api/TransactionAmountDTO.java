package org.financer.shared.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public class TransactionAmountDTO {

    @JsonProperty("id")
    @ApiModelProperty(value = "Identifier", required = true, example = "123")
    private int id;

    @JsonProperty("valueDate")
    @ApiModelProperty(value = "Value Date", required = true, example = "2020-02-02")
    private LocalDate valueDate;

    @JsonProperty("amount")
    @ApiModelProperty(value = "Amount", required = true, example = "74.99")
    private double amount;

    public int getId() {
        return id;
    }

    public TransactionAmountDTO setId(int id) {
        this.id = id;
        return this;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public TransactionAmountDTO setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionAmountDTO setAmount(double amount) {
        this.amount = amount;
        return this;
    }
}

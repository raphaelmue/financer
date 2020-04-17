package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(name = "FixedTransaction", description = "Schema for a fixed transaction")
public class FixedTransactionDTO {

    @NotNull
    @Min(1)
    @Schema(description = "Identifier of the fixed transaction", required = true, example = "1")
    private int id;

    @NotNull
    @Schema(description = "Amount of the fixed transaction", required = true)
    private Amount amount;

    @NotNull
    @Schema(description = "Time range of the fixed transaction", required = true)
    private TimeRange timeRange;

    @Schema(description = "Product of the fixed transaction", required = true, example = "Product")
    private String product;

    @Schema(description = "Description of the fixed transaction", required = true, example = "This is a description.")
    private String description;

    @Schema(description = "Vendor of the fixed transaction", required = true, example = "Amazon")
    private String vendor;

    @NotNull
    @Schema(description = "Indicates whether this transaction is variable or not", required = true, example = "false")
    private boolean isVariable;

    @Schema(description = "Billing day of the fixed transaction", required = true, example = "Amazon")
    private int day;

    @Schema(description = "List of fixed transaction amounts", required = true, example = "Amazon")
    private List<@Valid FixedTransactionAmountDTO> transactionAmounts;

    public int getId() {
        return id;
    }

    public FixedTransactionDTO setId(int id) {
        this.id = id;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public FixedTransactionDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public FixedTransactionDTO setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public FixedTransactionDTO setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public FixedTransactionDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public FixedTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public boolean getIsVariable() {
        return isVariable;
    }

    public FixedTransactionDTO setIsVariable(boolean variable) {
        isVariable = variable;
        return this;
    }

    public int getDay() {
        return day;
    }

    public FixedTransactionDTO setDay(int day) {
        this.day = day;
        return this;
    }

    public List<FixedTransactionAmountDTO> getTransactionAmounts() {
        return transactionAmounts;
    }

    public FixedTransactionDTO setTransactionAmounts(List<FixedTransactionAmountDTO> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
        return this;
    }
}

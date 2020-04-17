package org.financer.shared.domain.model.api.transaction.fixed;

import io.swagger.v3.oas.annotations.media.Schema;
import org.financer.shared.domain.model.api.DataTransferObject;
import org.financer.shared.domain.model.value.objects.Amount;
import org.financer.shared.domain.model.value.objects.TimeRange;

import javax.validation.Valid;
import java.util.Set;

@Schema(name = "UpdateFixedTransaction", description = "Schema to update fixed transaction")
public class UpdateFixedTransactionDTO implements DataTransferObject {

    @Schema(description = "Identifier of the category that will be assigned to the fixed transaction")
    private long categoryId;

    @Schema(description = "Amount of the fixed transaction")
    private Amount amount;

    @Schema(description = "Time range of the fixed transaction")
    private TimeRange timeRange;

    @Schema(description = "Product of the fixed transaction", example = "Product")
    private String product;

    @Schema(description = "Description of the fixed transaction", example = "This is a description.")
    private String description;

    @Schema(description = "Vendor of the fixed transaction", example = "Amazon")
    private String vendor;

    @Schema(description = "Indicates whether this transaction is variable or not", example = "false")
    private boolean isVariable;

    @Schema(description = "Billing day of the fixed transaction")
    private int day;

    @Schema(description = "List of fixed transaction amounts")
    private Set<@Valid FixedTransactionAmountDTO> transactionAmounts;

    public long getCategoryId() {
        return categoryId;
    }

    public UpdateFixedTransactionDTO setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public Amount getAmount() {
        return amount;
    }

    public UpdateFixedTransactionDTO setAmount(Amount amount) {
        this.amount = amount;
        return this;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public UpdateFixedTransactionDTO setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public UpdateFixedTransactionDTO setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UpdateFixedTransactionDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVendor() {
        return vendor;
    }

    public UpdateFixedTransactionDTO setVendor(String vendor) {
        this.vendor = vendor;
        return this;
    }

    public boolean getIsVariable() {
        return isVariable;
    }

    public UpdateFixedTransactionDTO setIsVariable(boolean variable) {
        isVariable = variable;
        return this;
    }

    public int getDay() {
        return day;
    }

    public UpdateFixedTransactionDTO setDay(int day) {
        this.day = day;
        return this;
    }

    public Set<FixedTransactionAmountDTO> getTransactionAmounts() {
        return transactionAmounts;
    }

    public UpdateFixedTransactionDTO setTransactionAmounts(Set<FixedTransactionAmountDTO> transactionAmounts) {
        this.transactionAmounts = transactionAmounts;
        return this;
    }
}
